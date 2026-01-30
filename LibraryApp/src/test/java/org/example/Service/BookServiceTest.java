package org.example.Service;

import org.example.Client.IGoogleBooksClient;
import org.example.Dto.BookResponseDto;
import org.example.Dto.GoogleBookResponse;
import org.example.Entity.Book;
import org.example.Repository.AuthorRepository;
import org.example.Repository.BookRepository;
import org.example.Repository.PublisherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private IGoogleBooksClient googleBooksClient;

    @InjectMocks
    private BookService bookService;

    //'A' ile başlayan kitapları filtreleme (Stream API Testi)
    @Test
    void getBooksStartingWithA_ShouldReturnFilteredBooks() {
        Book book1 = new Book();
        book1.setTitle("Ali Baba ve Kırk Haramiler");

        Book book2 = new Book();
        book2.setTitle("Cin Ali");

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        List<BookResponseDto> result = bookService.getBooksStartingWithA();

        assertEquals(1, result.size());
        assertEquals("Ali Baba ve Kırk Haramiler", result.get(0).getTitle());
    }


    @Test
    void fetchAndSaveBooks_ShouldSaveBook_WhenResponseIsValid() {
        String query = "Java";
        GoogleBookResponse mockResponse = new GoogleBookResponse();
        GoogleBookResponse.Item item = new GoogleBookResponse.Item();
        GoogleBookResponse.VolumeInfo info = new GoogleBookResponse.VolumeInfo();

        info.setTitle("Effective Java");
        info.setAuthors(Collections.singletonList("Joshua Bloch"));
        info.setPublisher("Addison-Wesley");
        item.setVolumeInfo(info);
        mockResponse.setItems(Collections.singletonList(item));

        when(googleBooksClient.searchBooks(query)).thenReturn(mockResponse);
        when(bookRepository.existsByIsbn13(any())).thenReturn(false);

        bookService.fetchAndSaveBooks(query);

        verify(bookRepository, times(1)).save(any(Book.class));
    }


    @Test
    void getRecentBooks_ShouldReturnRecentBooks() {

        Book recentBook = new Book();
        recentBook.setBookID(1L);
        recentBook.setTitle("Modern Java 2024");
        recentBook.setPublishYear(2024);

        when(bookRepository.findBooksPublishedAfter2023()).thenReturn(List.of(recentBook));


        List<BookResponseDto> result = bookService.getRecentBooks();

        assertEquals(1, result.size());
        assertEquals("Modern Java 2024", result.get(0).getTitle());
        assertNotNull(result.get(0).getBookID());
    }
}