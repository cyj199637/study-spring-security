package me.study.studyspringsecurity.form;

import java.util.Collection;
import me.study.studyspringsecurity.common.SecurityLogger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SampleService {

    /*
    SecurityContextHolder: Authentication을 담고 있는 곳
        - 반드시 인증된 사용자의 정보만이 들어가 있음
        - ThreadLocal을 사용하고 있어 어느 곳에서든 인증 정보를 사용할 수 있음
         (SecurityContextHolder의 기본 전략 = ThreadLocal)

    AuthenticationManager: 실제로 Authentication을 생성하고, 인증을 처리하는 인터페이스
        - 기본 구현체로 보통 ProviderManager를 사용
        - 인증 후에는 Aurhentication의 Principal이 UserDetails를 구현한 User 객체로 변환

        - AbstractAuthenticationProcessingFilter.doFilter()
        - UsernamePasswordAuthenticationFilter.attemptAuthentication()
        - UsernamePasswordAuthenticationToken
    */
    public void check() {
        // 사용자 인증 성공 -> authentication 객체 생성 -> SecurityContextHolder에 주입
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 사용자의 정보를 나타내는 객체
        Object principal = authentication.getPrincipal();
        // 사용자가 가지고 있는 권한을 나타내는 객체
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Object credentials = authentication.getCredentials();
        boolean authenticated = authentication.isAuthenticated();
    }

    /*
        UsernamePasswordAuthenticationFilter: 폼 인증을 처리하는 시큐리티 필터
            - 인증 과정은 AuthenticationManager에게 위임
            - successfulAuthentication(): 인증을 성공하면 인증된 Authentication 객체를 SecurityContextHolder에 저장

        SecurityContextPersistenceFilter
            - SecurityContext를 HTTP session에 캐시(기본 전략)하여 여러 요청에서 Authentication을 공유할 수 있도록 공유하는 필터
                - 요청이 들어올 때마다 어딘가에 이미 있는 SecurityContext를 가져와 SecurityContextHolder에 복구
                    - HttpSessionSecurityContextRepository: HttpSession에 SecurityContext를 저장하는 저장소
                - 로그인 요청이 성공적으로 처리되면, HttpSession에 SecurityContext를 저장
     */
    @Secured("ROLE_USER")
    public void dashboard() {
//        Account account = AccountContext.getAccount();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println("===============");
        System.out.println(authentication);
        System.out.println(userDetails.getUsername());
    }

    @Async
    public void asyncService() {
        SecurityLogger.log("Async Service");
        System.out.println("Async Service is called");
    }
}
