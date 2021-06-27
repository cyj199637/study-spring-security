package me.study.studyspringsecurity.account;

import me.study.studyspringsecurity.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    Account findByUsername(String username);
}
