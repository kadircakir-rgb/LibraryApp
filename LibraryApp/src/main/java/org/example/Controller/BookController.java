package org.example.Controller;

import org.example.Dto.BookResponseDto;
import org.example.Entity.Author;
import org.example.Entity.Publisher;
import org.example.Service.BookService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:5173")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/fetch")
    public String fetchBooks(@RequestParam String q) {
        bookService.fetchAndSaveBooks(q);
        return "Google'dan '" + q + "' araması yapıldı ve kitaplar veritabanına kaydedildi!";
    }


    @GetMapping
    public List<BookResponseDto> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/authors")
    public List<Author> getAllAuthors() {
        return bookService.getAllAuthors();
    }

    @GetMapping("/publishers")
    public List<Publisher> getAllPublishers() {
        return bookService.getAllPublishers();
    }

    @GetMapping("/publishers/limit")
    public List<Publisher> getTwoPublishers() {
        return bookService.getTwoPublishersWithDetails();
    }


    @GetMapping("/starts-with-a")
    public List<BookResponseDto> getBooksStartingWithA() {
        return bookService.getBooksStartingWithA();
    }


    @GetMapping("/recent")
    public List<BookResponseDto> getRecentBooks() {
        return bookService.getRecentBooks();
    }
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}