package org.example.Repository;

import org.example.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn13(String isbn13);

    @Query("SELECT b FROM Book b WHERE b.publishYear > 2023")
    List<Book> findBooksPublishedAfter2023();
}