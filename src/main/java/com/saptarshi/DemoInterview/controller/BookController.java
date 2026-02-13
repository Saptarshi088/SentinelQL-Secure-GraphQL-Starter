package com.saptarshi.DemoInterview.controller;

import com.saptarshi.DemoInterview.dto.AuthorResponseDto;
import com.saptarshi.DemoInterview.dto.BookResponseDto;
import com.saptarshi.DemoInterview.entity.Book;
import com.saptarshi.DemoInterview.repository.BookRepository;
import io.micrometer.observation.annotation.Observed;
import io.opentelemetry.api.trace.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookRepository bookRepository;
    private final Tracer tracer;

    @GetMapping(value = "/getAllBooks", produces = "application/json")
    @Observed(name = "Book.GET_ALL")
    public List<BookResponseDto> getAllBooks() {
        return bookRepository.findAllWithEagerRelationships()
                .stream()
                .map(b ->
                        BookResponseDto
                                .builder()
                                .pageCount(b.getPageCount())
                                .title(b.getTitle())
                                .author(b.getAuthor() == null ? null :
                                        AuthorResponseDto.builder()
                                                .firstName(b.getAuthor().getFirstName())
                                                .lastName(b.getAuthor().getLastName())
                                                .build())
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
                .author(newBook.getAuthor() == null ? null :
                        AuthorResponseDto.builder()
                                .firstName(newBook.getAuthor().getFirstName())
                                .lastName(newBook.getAuthor().getLastName())
                                .build())
                .build();
        var uri = uriComponentsBuilder.path("/books/{id}").buildAndExpand(newBook.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PostMapping("/byExample")
    public List<BookResponseDto> getBookByExample(@RequestBody Book book) {
        Example<Book> example = Example.of(book);
        return bookRepository.findAll(example)
                .stream()
                .map(s ->
                        BookResponseDto.builder()
                                .title(s.getTitle())
                                .pageCount(s.getPageCount())
                                .author(s.getAuthor() == null ? null :
                                        AuthorResponseDto.builder()
                                                .firstName(s.getAuthor().getFirstName())
                                                .lastName(s.getAuthor().getLastName())
                                                .build())
                                .build()
                )
                .toList();
    }
}
