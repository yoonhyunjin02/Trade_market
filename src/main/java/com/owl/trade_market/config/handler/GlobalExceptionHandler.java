package com.owl.trade_market.config.handler;

import com.owl.trade_market.config.exception.RegistrationException;
import com.owl.trade_market.config.exception.ResourceNotFoundException;
import com.owl.trade_market.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@ControllerAdvice // 이 클래스가 모든 컨트롤러에 대한 전역 설정을 담당함
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 회원가입 예외를 처리
     */
    @ExceptionHandler(RegistrationException.class)
    public String handleRegistrationException(RegistrationException ex,
                                              RedirectAttributes redirectAttributes) {

        UserDto dto = ex.getUserDto();
        redirectAttributes.addFlashAttribute("userDto", dto);

        // BindingResult 생성 (모델명 'userDto' 와 일치)
        BindingResult br = new BeanPropertyBindingResult(dto, "userDto");

        String msg = ex.getMessage();
        if (msg.contains("아이디")) {
            br.rejectValue("userId", "userId.duplicate", msg);
        } else if (msg.contains("사용자명")) {
            br.rejectValue("userName", "userName.duplicate", msg);
        } else {
            br.reject("registration.error", msg); // 글로벌 에러
        }

        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userDto", br);
        return "redirect:/register";
    }


    /**
     * 리소스를 찾지 못했을 때의 예외를 처리합니다.
     * (예: 존재하지 않는 상품, 카테고리 등)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException(ResourceNotFoundException ex, RedirectAttributes redirectAttributes) {
        log.warn("리소스를 찾을 수 없음: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());

        // 리소스를 못찾는 경우는 메인 상품 목록으로 보냅니다.
        return "redirect/products";
    }

    /**
     * 일반적인 비즈니스 로직 예외를 처리합니다.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex,
                                                 HttpServletRequest request,
                                                 RedirectAttributes redirectAttributes) {
        // 에러 로그
        log.warn("잘못된 인자 예외 발생: {}", ex.getMessage());

        // 리다이렉트할 페이지에 에러 메세지를 전달
        redirectAttributes.addFlashAttribute("error", ex.getMessage());

        // 사용자가 이전에 머물던 페이지(Referer)로 리다이렉트
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/main");
    }

    /**
     * 예상치 못한 모든 서버 예외를 처리합니다.
     * (예: NullPointException, 데이터베이스 연결 오류 등)
     */
    @ExceptionHandler(Exception.class)
    public String handleGlobalException(Exception ex, RedirectAttributes redirectAttributes) {

        // 에러 로그
        log.error("처리되지 않은 예외 발생", ex);

        // 사용자에게는 민감한 정보 없이 일반적인 에러 메시지 출력
        redirectAttributes.addFlashAttribute("error", "요청 처리 중 오류가 발생했습니다. 관리자에게 문의해주세요.");

        // 안전한 메인 페이지로 리다이렉트
        return "redirect:/main";
    }
}
