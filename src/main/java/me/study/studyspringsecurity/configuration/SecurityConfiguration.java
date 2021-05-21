package me.study.studyspringsecurity.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/*
FilterChainProxy: Spring Security에서 사용하는 필터들을 관리하고 실행하는 역할
    - FilterChainProxy.getFilters()
    - SecurityFilterChain 목록이 만들어지는 시점
    - SecurityConfig 하나 당 FilterChain이 하나라고 보면 됨
    - SecurityConfig가 여러 개 일 때는 우선순위에 따라 적용됨
        - 그러나 여러 Config들을 순서에 따라 적용시키기 보다는 매핑시킬 URL 패턴에 따라 설정을 달리하여 적용시키는 것이 좋음
    - Config 설정에 따라 불러오는 필터의 종류/개수가 달라짐

DelegatingFilterProxy
    - 서블릿 설정에 등록됨(나머지 필터들은 서블릿 필터를 구현한 구현체이긴 하나 서블릿이 아닌 스프링 내부적으로 사용하는 필터)
        -> 서블릿 컨테이너에는 DelegatingFilterProxy만 등록 되어있고
           Filter 호출 시, ApplicationContext 내에 있는 FilterChainProxy를 호출하여 처리
    - 서블릿 필터 처리를 FilterChainProxy에게 위임
    - SecurityFilterAutoConfiguration.securityFilterChainRegistration()
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                .mvcMatchers("/").permitAll()
                .mvcMatchers("/info").permitAll()
                .mvcMatchers("/accounts").permitAll()
                .mvcMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated();

        // Spring Security를 적용 후 Post로 요청을 보내려면 CSRF 토큰이 필요
        // CSRF 토큰을 안 보내도 401로 응답하 않게끔 해주는 설정
        httpSecurity.csrf()
            .ignoringAntMatchers("/accounts");
        httpSecurity.formLogin();
        httpSecurity.httpBasic();
    }
}
