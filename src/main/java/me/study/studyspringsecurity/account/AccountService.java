package me.study.studyspringsecurity.account;

import me.study.studyspringsecurity.account.domain.Account;
import me.study.studyspringsecurity.account.domain.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /*
        UserDetails: Principal 정보를 표현할 인터페이스
            - Principal과 현재 애플리케이션의 커스텀 유저 객체와의 어댑터 역할
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);

        if(account == null) {
            throw new UsernameNotFoundException(username);
        }

        return new UserAccount(account);
    }

    public Account create(Account account) {
        account.encodePassword(passwordEncoder);
        return accountRepository.save(account);
    }
}
