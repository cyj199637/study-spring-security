package me.study.studyspringsecurity.common;

import me.study.studyspringsecurity.account.AccountService;
import me.study.studyspringsecurity.account.domain.Account;
import me.study.studyspringsecurity.book.Book;
import me.study.studyspringsecurity.book.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DefaultDataGenerator implements ApplicationRunner {

    @Autowired
    private AccountService accountService;

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        Account rowling = createAccount("Rowling");
        Account tolkien = createAccount("Tolkien");

        createBook("Harry Poter", rowling);
        createBook("The Road of the Rings", tolkien);
    }

    private Account createAccount(String username) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword("123");
        account.setRole("USER");

        return accountService.create(account);
    }

    private void createBook(String title, Account author) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);

        bookRepository.save(book);
    }
}
