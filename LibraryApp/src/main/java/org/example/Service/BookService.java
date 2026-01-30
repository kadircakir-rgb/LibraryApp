package org.example.Service;

import org.example.Client.IGoogleBooksClient;
import org.example.Dto.BookResponseDto;
import org.example.Dto.GoogleBookResponse;
import org.example.Entity.*;
import org.example.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final IGoogleBooksClient googleBooksClient;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository,
                       PublisherRepository publisherRepository, IGoogleBooksClient googleBooksClient) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
        this.googleBooksClient = googleBooksClient;
    }

    @Transactional
    public void fetchAndSaveBooks(String query) {
        GoogleBookResponse response = googleBooksClient.searchBooks(query);

        if (response != null && response.getItems() != null) {
            for (GoogleBookResponse.Item item : response.getItems()) {
                GoogleBookResponse.VolumeInfo info = item.getVolumeInfo();
                if (info == null) continue;

                // 1. Yayınevi Kayıt: Null gelirse 'Bilinmeyen' olarak kaydedilir
                String pubName = (info.getPublisher() != null && !info.getPublisher().isEmpty())
                        ? info.getPublisher() : "Bilinmeyen Yayınevi";
                Publisher pub = publisherRepository.findByPublisherName(pubName).orElseGet(() -> {
                    Publisher p = new Publisher();
                    p.setPublisherName(pubName);
                    return publisherRepository.save(p);
                });

                // 2. Yazar Kayıt: Liste boşsa 'Bilinmeyen' olarak kaydedilir
                String autName = (info.getAuthors() != null && !info.getAuthors().isEmpty())
                        ? info.getAuthors().get(0) : "Bilinmeyen Yazar";
                Author aut = authorRepository.findByAuthorNameSurname(autName).orElseGet(() -> {
                    Author a = new Author();
                    a.setAuthorNameSurname(autName);
                    return authorRepository.save(a);
                });


                String isbn = "N/A-" + System.currentTimeMillis();
                if (info.getIndustryIdentifiers() != null && !info.getIndustryIdentifiers().isEmpty()) {
                    isbn = info.getIndustryIdentifiers().get(0).getIdentifier();
                }


                if (!bookRepository.existsByIsbn13(isbn)) {
                    Book book = new Book();
                    book.setTitle(info.getTitle() != null ? info.getTitle() : "Başlıksız Kitap");
                    book.setIsbn13(isbn);
                    book.setPrice(100.0);
                    book.setAuthor(aut);
                    book.setPublisher(pub);


                    if (info.getPublishedDate() != null && info.getPublishedDate().length() >= 4) {
                        try {
                            int year = Integer.parseInt(info.getPublishedDate().substring(0, 4));
                            book.setPublishYear(year);
                        } catch (Exception e) {
                            book.setPublishYear(null);
                        }
                    }
                    bookRepository.save(book);
                }
            }
        }
    }

    // --- DTO DÖNÜŞÜMÜ ---

    private BookResponseDto convertToDto(Book book) {
        BookResponseDto dto = new BookResponseDto();
        dto.setBookID(book.getBookID());
        dto.setTitle(book.getTitle());
        dto.setPrice(book.getPrice());
        dto.setISBN13(book.getIsbn13());
        dto.setAuthorNameSurname(book.getAuthor() != null ? book.getAuthor().getAuthorNameSurname() : "Yazar Bilgisi Yok");
        dto.setPublisherName(book.getPublisher() != null ? book.getPublisher().getPublisherName() : "Yayınevi Bilgisi Yok");
        return dto;
    }

    public List<BookResponseDto> getAllBooks() {
        return bookRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public List<Publisher> getAllPublishers() {
        return publisherRepository.findAll();
    }

    public List<Publisher> getTwoPublishersWithDetails() {
        return publisherRepository.findAll().stream().limit(2).collect(Collectors.toList());
    }

    public List<BookResponseDto> getBooksStartingWithA() {
        return bookRepository.findAll().stream()
                .filter(b -> b.getTitle() != null && b.getTitle().toUpperCase().startsWith("A"))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<BookResponseDto> getRecentBooks() {
        return bookRepository.findBooksPublishedAfter2023().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}