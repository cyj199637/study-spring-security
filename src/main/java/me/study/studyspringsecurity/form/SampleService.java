package me.study.studyspringsecurity.form;

import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SampleService {

    public void dashboard() {
        // SecurityContextHolder 안에는 반드시 인증된 사용자의 정보만이 들어가 있음
        // SecurityContextHolder는 ThreadLocal을 사용하고 있어 어느 곳에서든 인증 정보를 사용할 수 있음
        // (SecurityContextHolder의 default mode = ThreadLocal)
        // 사용자 인증 성공 -> authentication 객체 생성 -> SecurityContextHolder에 주입
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 사용자의 정보를 나타내는 객체
        Object principal = authentication.getPrincipal();
        // 사용자가 가지고 있는 권한을 나타내는 객체
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Object credentials = authentication.getCredentials();
        boolean authenticated = authentication.isAuthenticated();
    }
}
