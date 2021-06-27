package me.study.studyspringsecurity.account;

import me.study.studyspringsecurity.account.domain.Account;
import me.study.studyspringsecurity.account.dto.CreateAccountRequest;
import me.study.studyspringsecurity.account.dto.CreateAccountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity<CreateAccountResponse> signUp(@RequestBody CreateAccountRequest request) {
        Account createdAccount = accountService.create(request.toEntity());
        return ResponseEntity.ok(CreateAccountResponse.of(createdAccount));
    }
}
