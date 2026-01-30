package org.example.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "publishers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long publisherID;

    private String publisherName;


    @OneToMany(mappedBy = "publisher")
    @JsonIgnore
    private List<Book> books;
}