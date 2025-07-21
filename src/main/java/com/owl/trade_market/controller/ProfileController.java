package com.owl.trade_market.controller;

import com.owl.trade_market.dto.ProfileEditDto;
import com.owl.trade_market.entity.Gender;
import com.owl.trade_market.entity.User;
import com.owl.trade_market.entity.UserDetails;
import com.owl.trade_market.entity.Product;
import com.owl.trade_market.service.UserService;
import com.owl.trade_market.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.Collections;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    /**
     * 프로필 메인 페이지
     */
    @GetMapping
    public String profilePage(@RequestParam(required = false) String userIdString,
                              Model model,
                              HttpSession session,
                              @AuthenticationPrincipal OAuth2User oauth2User,
                              RedirectAttributes redirectAttributes) {
        try {
            // 1. 현재 로그인한 사용자 확인
            User currentUser = getCurrentUser(session, oauth2User);
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
                return "redirect:/login";
            }

            // 2. 조회할 대상 사용자 결정
            User targetUser;
            boolean isOwnProfile;

            if (userIdString == null || userIdString.trim().isEmpty()) {
                // 파라미터 없으면 자신의 프로필
                targetUser = currentUser;
                isOwnProfile = true;
            } else {
                // 특정 사용자 프로필 조회
                Optional<User> userOptional = userService.findByUserId(userIdString);
                if (userOptional.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "존재하지 않는 사용자입니다.");
                    return "redirect:/main";
                }
                targetUser = userOptional.get();
                isOwnProfile = currentUser.getId().equals(targetUser.getId());
            }

            // 3. UserDetails 안전하게 가져오기
            UserDetails userDetails = getUserDetailsWithFallback(targetUser);

            // 4. 해당 사용자의 상품 목록 (ProductService 없으면 빈 리스트)
            List<Product> userProducts = Collections.emptyList(); // 임시로 빈 리스트

             try {
                 userProducts = productService.findBySeller(targetUser, Sort.by("createdAt").descending());
             } catch (Exception e) {
                 System.err.println("상품 목록 조회 중 오류: " + e.getMessage());
                 userProducts = Collections.emptyList();
             }

            // 5. 모델에 데이터 추가
            model.addAttribute("user", targetUser);
            model.addAttribute("userDetails", userDetails);
            model.addAttribute("userProducts", userProducts);
            model.addAttribute("isOwnProfile", isOwnProfile);

            return "pages/profile";

        } catch (Exception e) {
            System.err.println("프로필 페이지 로딩 중 오류: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "프로필 정보를 불러오는 중 오류가 발생했습니다.");
            return "redirect:/main";
        }
    }

    /**
     * 프로필 수정 폼 표시
     */
    @GetMapping("/edit")
    public String editProfileForm(Model model,
                                  HttpSession session,
                                  @AuthenticationPrincipal OAuth2User oauth2User,
                                  RedirectAttributes redirectAttributes) {
        try {
            // 1. 현재 사용자 정보 가져오기
            User currentUser = getCurrentUser(session, oauth2User);
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
                return "redirect:/login";
            }

            // 2. UserDetails 안전하게 가져오기
            UserDetails userDetails = getUserDetailsWithFallback(currentUser);

            // 3. ProfileEditDto 생성
            ProfileEditDto profileEditDto = new ProfileEditDto(
                    currentUser.getUserName(),
                    userDetails.getIntroduction(),
                    userDetails.getAge(),
                    userDetails.getGender(),
                    currentUser.getUserLocation()
            );

            // 4. 모델에 데이터 추가
            model.addAttribute("user", currentUser);
            model.addAttribute("userDetails", userDetails);
            model.addAttribute("profileEditDto", profileEditDto);
            model.addAttribute("genderOptions", Gender.values());

            return "pages/profile-edit";

        } catch (Exception e) {
            System.err.println("프로필 수정 폼 로딩 중 오류: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "프로필 정보를 불러오는 중 오류가 발생했습니다.");
            return "redirect:/profile";
        }
    }

    /**
     * 프로필 수정 처리
     */
    @PostMapping("/edit")
    public String updateProfile(@ModelAttribute("profileEditDto") ProfileEditDto profileEditDto,
                                HttpSession session,
                                @AuthenticationPrincipal OAuth2User oauth2User,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        try {
            // 1. 현재 사용자 정보 가져오기
            User currentUser = getCurrentUser(session, oauth2User);
            if (currentUser == null || currentUser.getId() == null) {
                redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
                return "redirect:/login";
            }

            // 2. User 정보 업데이트 (기본 정보)
            if (profileEditDto.getUserName() != null && !profileEditDto.getUserName().trim().isEmpty()) {
                currentUser.setUserName(profileEditDto.getUserName().trim());
            }
            if (profileEditDto.getUserLocation() != null) {
                currentUser.setUserLocation(profileEditDto.getUserLocation().trim());
            }

            // User 엔티티 저장
            User updatedUser = userService.updateUser(currentUser);

            // 3. UserDetails 안전하게 가져오기 또는 생성
            UserDetails userDetails = getUserDetailsWithFallback(updatedUser);

            // 4. UserDetails 정보 업데이트
            if (profileEditDto.getIntroduction() != null) {
                userDetails.setIntroduction(profileEditDto.getIntroduction().trim().isEmpty() ? null : profileEditDto.getIntroduction().trim());
            }
            if (profileEditDto.getAge() != null && profileEditDto.getAge() > 0) {
                userDetails.setAge(profileEditDto.getAge());
            }
            if (profileEditDto.getGender() != null && !profileEditDto.getGender().trim().isEmpty()) {
                try {
                    userDetails.setGender(Gender.valueOf(profileEditDto.getGender().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    System.err.println("잘못된 성별 값: " + profileEditDto.getGender());
                }
            }

            // 5. UserDetails 저장
            userService.updateUserDetails(userDetails);

            // 6. 세션 업데이트 (일반 로그인 사용자의 경우)
            if (session.getAttribute("user") instanceof User) {
                session.setAttribute("user", updatedUser);
            }

            redirectAttributes.addFlashAttribute("success", "프로필이 성공적으로 수정되었습니다.");
            return "redirect:/profile";

        } catch (Exception e) {
            System.err.println("프로필 수정 중 오류: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "프로필 수정 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/profile/edit";
        }
    }

    /**
     * 프로필 삭제 (회원 탈퇴)
     */
    @PostMapping("/delete")
    public String deleteProfile(HttpSession session,
                                @AuthenticationPrincipal OAuth2User oauth2User,
                                RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser(session, oauth2User);
            if (currentUser == null) {
                return "redirect:/login";
            }

            // TODO: 회원 탈퇴 로직 구현
            // 현재는 단순히 세션 무효화만 처리
            session.invalidate();

            redirectAttributes.addFlashAttribute("success", "회원 탈퇴가 완료되었습니다.");
            return "redirect:/main";

        } catch (Exception e) {
            System.err.println("회원 탈퇴 중 오류: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "회원 탈퇴 중 오류가 발생했습니다.");
            return "redirect:/profile";
        }
    }

    /**
     * UserDetails를 안전하게 가져오는 메서드
     */
    private UserDetails getUserDetailsWithFallback(User user) {
        try {
            if (user == null || user.getId() == null) {
                throw new IllegalArgumentException("User 정보가 없습니다.");
            }

            // 먼저 기존 UserDetails 조회 시도
            Optional<UserDetails> existingDetails = userService.findUserDetailsByUserId(user.getId());
            if (existingDetails.isPresent()) {
                System.out.println("기존 UserDetails 조회 성공: User ID " + user.getId());
                return existingDetails.get();
            }

            // 없으면 생성
            System.out.println("UserDetails 없음, 생성 중: User ID " + user.getId());
            UserDetails newDetails = userService.createUserDetails(user);

            if (newDetails != null) {
                System.out.println("UserDetails 생성 성공: User ID " + user.getId());
                return newDetails;
            }

            // 생성 실패 시 기본 UserDetails 반환
            System.err.println("UserDetails 생성 실패, 임시 객체 사용: User ID " + user.getId());
            return new UserDetails(user);

        } catch (Exception e) {
            System.err.println("UserDetails 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            // 최후의 수단: 기본 UserDetails 반환
            return new UserDetails(user);
        }
    }

    /**
     * 현재 로그인한 사용자를 가져오는 메서드
     */
    private User getCurrentUser(HttpSession session, OAuth2User oauth2User) {
        try {
            // 1. OAuth2 로그인 확인
            if (oauth2User != null) {
                Object userObj = oauth2User.getAttribute("user");
                if (userObj instanceof User) {
                    User user = (User) userObj;
                    System.out.println("OAuth2 사용자 정보 확인: " + user.getUserEmail());
                    return user;
                }
            }

            // 2. 일반 세션 로그인 확인
            Object sessionUser = session.getAttribute("user");
            if (sessionUser instanceof User) {
                User user = (User) sessionUser;
                System.out.println("세션 사용자 정보 확인: " + user.getUserId());
                return user;
            }

            System.err.println("현재 사용자 정보를 찾을 수 없습니다.");
            return null;

        } catch (Exception e) {
            System.err.println("현재 사용자 조회 중 오류: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}