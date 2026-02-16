package com.saptarshi.DemoInterview.controller;

import com.saptarshi.DemoInterview.dto.*;
import com.saptarshi.DemoInterview.entity.Author;
import com.saptarshi.DemoInterview.entity.Book;
import com.saptarshi.DemoInterview.exception.ResourceNotFoundException;
import com.saptarshi.DemoInterview.repository.AuthorRepository;
import com.saptarshi.DemoInterview.repository.BookRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Controller
@RequiredArgsConstructor
@CrossOrigin
@Validated
public class GraphQLController {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @QueryMapping
    public List<BookResponseDto> getAllBooks() {
        return bookRepository.findAllWithEagerRelationships()
                .stream()
                .map(this::toBookResponse)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @MutationMapping
    public BookResponseDto addBook(@Valid @Argument(name = "input") AddBookRequest request) {

        var author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

        var savedBook = bookRepository.save(Book.builder()
                .title(request.getTitle())
                .pageCount(request.getPageCount())
                .author(author)
                .build());
        return toBookResponse(savedBook);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @MutationMapping
    public BookResponseDto updateBook(@Valid @Argument(name = "input") UpdateBookRequest request) {
        var book = bookRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        book.setTitle(request.getTitle());
        book.setPageCount(request.getPageCount());
        var updatedBook = bookRepository.save(book);
        return toBookResponse(updatedBook);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @MutationMapping
    public String deleteBook(@Argument(name = "input") Long id) {
        var book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        bookRepository.delete(book);
        return "Book with ID : " + id + " Title : " + book.getTitle() + " Deleted";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @QueryMapping
    public List<AuthorResponseDto> getAllAuthors() {
        return authorRepository.findAll()
                .stream()
                .map(a -> AuthorResponseDto.builder()
                        .firstName(a.getFirstName())
                        .lastName(a.getLastName())
                        .build())
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public AuthorResponseDto addAuthor(@Valid @Argument(name = "input") AddAuthorRequest request) {
        var author = new Author();
        author.setFirstName(request.getFirstName());
        author.setLastName(request.getLastName());
        var savedAuthor = authorRepository.save(author);

        return AuthorResponseDto.builder()
                .firstName(savedAuthor.getFirstName())
                .lastName(savedAuthor.getLastName())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public AuthorResponseDto deleteAuthor(@Argument(name = "input") Long id) {
        var author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));
        authorRepository.delete(author);
        return AuthorResponseDto.builder()
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .build();
    }

    private BookResponseDto toBookResponse(Book book) {
        return BookResponseDto.builder()
                .title(book.getTitle())
                .pageCount(book.getPageCount())
                .author(book.getAuthor() == null ? null :
                        AuthorResponseDto.builder()
                                .firstName(book.getAuthor().getFirstName())
                                .lastName(book.getAuthor().getLastName())
                                .build())
                .build();
    }
}
