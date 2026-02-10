package com.saptarshi.DemoInterview.controller;

import com.saptarshi.DemoInterview.dto.BookResponseDto;
import com.saptarshi.DemoInterview.entity.Book;
import com.saptarshi.DemoInterview.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    public final BookRepository bookRepository;

    @GetMapping("/getAllBooks")
    public List<BookResponseDto> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(b-> new BookResponseDto()
                        .builder()
                        .pageCount(b.getPageCount())
                        .title(b.getTitle())
                        .build()
                )
                .toList();
    }

    @PostMapping("/addBook")
    public ResponseEntity<BookResponseDto> addBook(@RequestBody Book book, UriComponentsBuilder uriComponentsBuilder) {
        var newBook = bookRepository.save(book);
        var response = BookResponseDto
                .builder()
                .title(newBook.getTitle())
                .pageCount(newBook.getPageCount())
                .build();
        var uri = uriComponentsBuilder.path("/books/{id}").buildAndExpand(newBook.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }
}
