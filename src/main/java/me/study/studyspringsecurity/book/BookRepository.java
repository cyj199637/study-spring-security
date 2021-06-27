package me.study.studyspringsecurity.book;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query("select b from Book b where b.author.id = ?#{principal.account.id}")
    List<Book> findAllOfCurrentUser();
}
