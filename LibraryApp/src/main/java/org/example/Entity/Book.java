package org.example.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor // JPA (Hibernate)
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookID;

    private String title;
    private Double price;
    private String isbn13;


    @ManyToOne
    @JoinColumn(name = "publisherID")
    private Publisher publisher;


    @ManyToOne
    @JoinColumn(name = "authorID")
    private Author author;


    private Integer publishYear;
}