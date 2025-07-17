package com.owl.trade_market.config.auth;


import com.owl.trade_market.entity.AuthProvider;
import com.owl.trade_market.entity.User;
import com.owl.trade_market.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService { // OAuth2UserRequest -> OAuth2User 변환

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        //OAuth2 공급자(구글)가 제공하는 사용자 정보(JSON)을 OAuth2User 객체로 받아옵니다.

        try {
            return processOAuth2User(userRequest, oauth2User);
            // 본인 DB 기준의 사용자 등록/갱신 로직을 수행하고, 여기서 반환된 OAuth2User를 최종 리턴한다.
        } catch (Exception ex) {
            throw new OAuth2AuthenticationException("OAuth2 사용자 처리 중 오류 발생: " + ex.getMessage());
        }
    }

    // 사용자 등록/갱신 상세로직
    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        // 어떤 공급자가 로그인 했는지 식별
        String registrationId = userRequest
                .getClientRegistration() // application.yml 혹은 SecurityConfig에 등록된 클라이언트 정보
                .getRegistrationId(); // "google", "naver" 등 구분자
        // OAuth2User에 담긴 원본 속성 전체 가져오기
        Map<String, Object> attributes = oauth2User.getAttributes();

        // Google 사용자 정보 추출
        String id = (String) attributes.get("sub");
        String name = (String) attributes.get("name");
        String email = (String) attributes.get("email");



        if (email == null || email.isEmpty()) {
            throw new OAuth2AuthenticationException("이메일 정보를 가져올 수 없습니다.");
        }

        Optional<User> userOptional = userRepository.findByUserEmail(email); // 이미 가입된 사용자인지 확인

        User user;

        if (userOptional.isPresent()) { // 기존 사용자를 확인
            user = userOptional.get(); // User 객체 꺼내기
            // 기존 사용자가 다른 OAuth 제공자로 가입한 경우 체크
            if (!user.getProvider()
                    .equals(AuthProvider.valueOf(registrationId.toUpperCase()))) {
                // 공급자가 다른데 같은 이메일인 경우 -> 충돌예외
                throw new OAuth2AuthenticationException(
                        "이미 " + user.getProvider() + " 계정으로 가입된 이메일입니다."
                );
            }
            // 기존 사용자 정보 업데이트
            user.setUserName(name); // 같은 공급자라면, 사용자 이름만 업데이트
            user = userRepository.save(user); // 변경된 User 엔티티를 DB에 저장
        } else {
            // 새 사용자 생성
            user = new User();
            user.setUserName(name);
            user.setUserEmail(email);
            user.setProvider(AuthProvider.valueOf(registrationId.toUpperCase()));
            user.setProviderId(id);
            // 이메일에서 @ 앞부분만 userId로 추출
            String[] parts = email.split("@");
            String localPart = parts.length > 0 ? parts[0] : email;
            user.setUserId(localPart);
            user = userRepository.save(user);
        }

        // 사용자 정보를 attributes에 추가
        Map<String, Object> modifiedAttributes = new HashMap<>(attributes); // 사용자 정보 복사
        modifiedAttributes.put("user", user); // DB에 조회·저장한 User 엔티티 넣어준다.
        // 나중에 컨트롤러나 서비스 레이어에서 DB 사용자 정보를 꺼내 쓸수 있게 된다.

        // OAuth2User 객체 반환 (권한과 함께)
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                modifiedAttributes,
                "sub" // nameAttributeKey
        );
    }
}