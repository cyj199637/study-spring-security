package me.study.studyspringsecurity.account.dto;

import lombok.Getter;
import me.study.studyspringsecurity.account.domain.Account;

@Getter
public class CreateAccountRequest {

    private String username;
    private String password;
    private String role;

    public Account toEntity() {
        return Account.builder()
            .username(this.username)
            .password(this.password)
            .role(this.role)
            .build();
    }
}
