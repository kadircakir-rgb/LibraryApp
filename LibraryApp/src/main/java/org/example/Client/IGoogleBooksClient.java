package org.example.Client;

import org.example.Dto.GoogleBookResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "googleBooksClient", url = "https://www.googleapis.com/books/v1")
public interface IGoogleBooksClient {

    @GetMapping("/volumes")
    GoogleBookResponse searchBooks(@RequestParam("q") String query);
}