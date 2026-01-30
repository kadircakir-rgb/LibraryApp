package org.example.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor // Hibernate için
@AllArgsConstructor
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authorID;

    private String authorNameSurname;

    // İsteğe bağlı
    @OneToMany(mappedBy = "author")
    @JsonIgnore
    private List<Book> books;
}