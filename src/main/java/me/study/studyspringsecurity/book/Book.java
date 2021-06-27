package me.study.studyspringsecurity.book;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import me.study.studyspringsecurity.account.domain.Account;

@Entity
@Getter
@Setter
public class Book {

    @Id @GeneratedValue
    private Integer id;

    private String title;

    @ManyToOne
    private Account author;
}
