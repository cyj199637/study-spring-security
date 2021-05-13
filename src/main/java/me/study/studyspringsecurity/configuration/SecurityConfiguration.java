package me.study.studyspringsecurity.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

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
