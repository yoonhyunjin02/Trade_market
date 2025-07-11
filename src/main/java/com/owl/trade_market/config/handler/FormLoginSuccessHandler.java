package com.owl.trade_market.config.handler;

import com.owl.trade_market.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FormLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Lazy
    @Autowired
    private UserService userService;

    public FormLoginSuccessHandler() {
        setDefaultTargetUrl("/main"); // 로그인 성공 시 이동할 URL
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 인증된 사용자 정보에서 username(우리의 경우 userId)을 가져옵니다.
        String userId = ((UserDetails) authentication.getPrincipal()).getUsername();

        // UserService를 사용해 DB에서 전체 User 엔티티 정보를 조회합니다.
        userService.findByUserId(userId).ifPresent(user -> {
            // 3. HttpSession을 가져와 "user"라는 이름으로 User 객체를 저장합니다.
            //    이제 ProductController의 getCurrentUser() 메서드가 이 세션 값을 사용할 수 있습니다.
            HttpSession session = request.getSession(true); // 세션이 없으면 새로 생성
            session.setAttribute("user", user);
        });

        // 세션에서 이전 URL 가져오기
        HttpSession session = request.getSession(false); //false(없으면 session생성 안함)
        if (session != null) {
            String redirectUrl = (String) session.getAttribute("previousUrl");

            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                // 사용 후 세션에서 제거
                session.removeAttribute("previousUrl");
                // 저장된 URL로 리디렉션
                getRedirectStrategy().sendRedirect(request, response,redirectUrl);
                return; // 리디렉션 후 추가 작업 방지
            }
        }

        // 부모 클래스의 메서드를 호출하여 SecurityConfig에 설정된 URL로 리디렉션을 수행합니다.
        super.onAuthenticationSuccess(request, response, authentication);
    }
}