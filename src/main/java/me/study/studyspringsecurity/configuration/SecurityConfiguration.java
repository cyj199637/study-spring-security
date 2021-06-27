package me.study.studyspringsecurity.configuration;

import java.util.Arrays;
import java.util.List;
import me.study.studyspringsecurity.account.AccountService;
import me.study.studyspringsecurity.common.CustomAccessDeniedHandler;
import me.study.studyspringsecurity.common.LoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;

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
@Order(2)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    CustomAccessDeniedHandler handler;

    @Autowired
    AccountService accountService;

    // 권한 위계 설정을 위한 커스텀 AccessDecisionmanager 정의
    public AccessDecisionManager accessDecisionManager() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);

        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
        webExpressionVoter.setExpressionHandler(handler);
        List<AccessDecisionVoter<? extends Object>> voters = Arrays.asList(webExpressionVoter);

        return new AffirmativeBased(voters);
    }

    // 실제 권한 위계 설정은 ExpressionHandler에서 이뤄지기 때문에 커스텀 ExpressionHandler만 정의하면 간단해짐
    public SecurityExpressionHandler expressionHandler() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);

        return handler;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // 커스텀 필터 추가
        httpSecurity.addFilterBefore(new LoggingFilter(), WebAsyncManagerIntegrationFilter.class);

        httpSecurity.authorizeRequests()
                .mvcMatchers("/").permitAll()
                .mvcMatchers("/info").permitAll()
                .mvcMatchers("/signup").permitAll()
                .mvcMatchers("/accounts").permitAll()
                .mvcMatchers("/admin").hasRole("ADMIN")
                .mvcMatchers("/user").hasRole("USER")
                // ignoring() 설정과 결과는 같지만 처리 프로세스는 다름
                // ignoring()은 지정한 요청을 아예 시큐리티 적용 자체를 하지 않지만, 아래 설정은 FilterChain에 있는 모든 필터를 타게 됨
//                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .anyRequest().authenticated()
                .expressionHandler(expressionHandler());

        // Spring Security를 적용 후 Post로 요청을 보내려면 CSRF 토큰이 필요
        // CSRF 토큰을 안 보내도 401로 응답하지 않게끔 해주는 설정
        httpSecurity.csrf()
            .ignoringAntMatchers("/accounts");

        httpSecurity.formLogin()
                    .loginPage("/login").permitAll();

        httpSecurity.rememberMe()
            .userDetailsService(accountService)
            .key("remember-me");

        httpSecurity.httpBasic();

        httpSecurity.logout()
                    .logoutSuccessUrl("/");

        // 인증이 안 된 사용자를 null 대신 AnonymousAuthentication 객체로 대체
        httpSecurity.anonymous();

        // 동시성 제어
        httpSecurity.sessionManagement()
            .sessionFixation()
                .changeSessionId()
            .maximumSessions(1)
                .expiredUrl("/");
//                  .maxSessionsPreventsLogin(true);

        httpSecurity.exceptionHandling()
            .accessDeniedHandler(handler);

        // MODE_INHERITABLETHREADLOCAL: 현재 스레드에서 생성된 하위 스레드에도 동일한 SecurityContext가 공유됨
        //                           -> @Async로 만들어진 하위 스레드에도 공유 가능
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
//        webSecurity.ignoring().mvcMatchers("/static/favicon.ico");
//        webSecurity.ignoring().antMatchers("**/favicon.ico");
//        webSecurity.ignoring().regexMatchers("/static/favicon.ico");
        webSecurity.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
