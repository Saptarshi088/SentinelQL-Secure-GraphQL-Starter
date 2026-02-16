package com.saptarshi.DemoInterview.controller;

import com.saptarshi.DemoInterview.dto.AuthorResponseDto;
import com.saptarshi.DemoInterview.dto.BookResponseDto;
import com.saptarshi.DemoInterview.dto.CreateBookRequest;
import com.saptarshi.DemoInterview.entity.Book;
import com.saptarshi.DemoInterview.exception.ResourceNotFoundException;
import com.saptarshi.DemoInterview.repository.AuthorRepository;
import com.saptarshi.DemoInterview.repository.BookRepository;
import io.micrometer.observation.annotation.Observed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @GetMapping(value = "/getAllBooks", produces = "application/json")
    @Observed(name = "Book.GET_ALL")
    public List<BookResponseDto> getAllBooks() {
        return bookRepository.findAllWithEagerRelationships()
                .stream()
                .map(this::toBookResponse)
                .toList();
    }

    @PostMapping("/addBook")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<BookResponseDto> addBook(@Valid @RequestBody CreateBookRequest request,
                                                   UriComponentsBuilder uriComponentsBuilder) {
        var author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

        var newBook = bookRepository.save(Book.builder()
                .title(request.getTitle())
                .pageCount(request.getPageCount())
                .author(author)
                .build());

        var uri = uriComponentsBuilder.path("/books/{id}").buildAndExpand(newBook.getId()).toUri();
        return ResponseEntity.created(uri).body(toBookResponse(newBook));
    }

    @PostMapping("/byExample")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<BookResponseDto> getBookByExample(@RequestBody Book book) {
        Example<Book> example = Example.of(book);
        return bookRepository.findAll(example)
                .stream()
                .map(this::toBookResponse)
                .toList();
    }

    private BookResponseDto toBookResponse(Book book) {
        return BookResponseDto
                .builder()
                .pageCount(book.getPageCount())
                .title(book.getTitle())
                .author(book.getAuthor() == null ? null :
                        AuthorResponseDto.builder()
                                .firstName(book.getAuthor().getFirstName())
                                .lastName(book.getAuthor().getLastName())
                                .build())
                .build();
    }
}
