package me.study.studyspringsecurity.account.dto;

import lombok.Builder;
import me.study.studyspringsecurity.account.domain.Account;

@Builder
public class CreateAccountResponse {

    private Integer id;
    private String username;
    private String role;

    public static CreateAccountResponse of(Account account) {
        return CreateAccountResponse.builder()
            .id(account.getId())
            .username(account.getUsername())
            .role(account.getRole())
            .build();
    }
}
