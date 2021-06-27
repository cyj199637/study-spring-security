package me.study.studyspringsecurity.form;

import me.study.studyspringsecurity.account.domain.Account;
import me.study.studyspringsecurity.account.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SampleServiceTest {

    @Autowired
    private SampleService sampleService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Test
    public void dashboard() {
        Account account = new Account();
        account.setUsername("yoonjeong");
        account.setPassword("123");
        account.setRole("ADMIN");
        accountService.create(account);

        UserDetails userDetails = accountService.loadUserByUsername("yoonjeong");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, "123");
        Authentication authentication = authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        sampleService.dashboard();
    }
}