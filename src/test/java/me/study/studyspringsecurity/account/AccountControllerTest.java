package me.study.studyspringsecurity.account;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import me.study.studyspringsecurity.account.domain.Account;
import me.study.studyspringsecurity.annotation.WithUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    /*
        @WithAnonymousUser
        = mockMVc.perform(get()).with(anonymous())...
     */
    @Test
    @WithAnonymousUser
    public void index_anonymous() throws Exception {
        mockMvc.perform(get("/"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    /*
        @WithMockUser(username = "yoonjeong", roles = "USER")
        = mockMvc.perform(get("/").with(user("yoonjeong").roles("USER")))...
        -> Mock User가 이미 로그인된 상태
     */
    @Test
    @WithMockUser(username = "yoonjeong", roles = "USER")
    public void index_user() throws Exception {
        mockMvc.perform(get("/"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @WithUser
    public void admin_user() throws Exception {
        mockMvc.perform(get("/admin"))
            .andDo(print())
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "yoonjeong", roles = "ADMIN")
    public void admin_admin() throws Exception {
        mockMvc.perform(get("/admin"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void login_success() throws Exception {
        String username = "yoonjeong";
        String password = "123";
        Account account = create(username, password);
        mockMvc.perform(formLogin().user(account.getUsername()).password(password))
            .andExpect(authenticated());
    }

    @Test
    @Transactional
    public void login_fail() throws Exception {
        String username = "yoonjeong";
        String password = "123";
        Account account = create(username, password);
        mockMvc.perform(formLogin().user(username).password("456"))
            .andExpect(unauthenticated());
    }

    private Account create(String username, String password) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        account.setRole("USER");

        return accountService.create(account);
    }
}