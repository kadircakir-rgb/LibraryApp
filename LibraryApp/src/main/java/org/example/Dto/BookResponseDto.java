package org.example.Dto;

import lombok.Data;

@Data // otomatik yapar
public class BookResponseDto {
    private Long bookID;
    private String title;
    private Double price;
    private String ISBN13;
    private String authorNameSurname;
    private String publisherName;
}