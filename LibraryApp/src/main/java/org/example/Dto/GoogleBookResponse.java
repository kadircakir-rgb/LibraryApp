package org.example.Dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class GoogleBookResponse {
    private List<Item> items;

    @Getter
    @Setter
    public static class Item {
        private VolumeInfo volumeInfo;
    }

    @Getter
    @Setter
    public static class VolumeInfo {
        private String title;
        private List<String> authors;
        private String publisher;
        private String publishedDate;


        private List<IndustryIdentifier> industryIdentifiers;
    }

    @Getter
    @Setter
    public static class IndustryIdentifier {
        private String type;
        private String identifier;
    }
}