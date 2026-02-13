package com.saptarshi.DemoInterview.controller;

import com.saptarshi.DemoInterview.dto.*;
import com.saptarshi.DemoInterview.entity.Author;
import com.saptarshi.DemoInterview.entity.Book;
import com.saptarshi.DemoInterview.repository.AuthorRepository;
import com.saptarshi.DemoInterview.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Controller
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class GraphQLController {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @QueryMapping
    public List<BookResponseDto> getAllBooks() {
        return bookRepository.findAllWithEagerRelationships()
                .stream()
                .map(b ->
                        BookResponseDto
                                .builder()
                                .title(b.getTitle())
                                .pageCount(b.getPageCount())
                                .author(b.getAuthor() == null ? null :
                                        AuthorResponseDto.builder()
                                                .firstName(b.getAuthor().getFirstName())
                                                .lastName(b.getAuthor().getLastName())
                                                .build())
                                .build()

                )
                .toList();
    }

    @MutationMapping
    public BookResponseDto addBook(@Argument(name = "input") AddBookRequest request) {

        var author = authorRepository.findById(request.getAuthorId()).orElseThrow(() -> new RuntimeException("Author Not Found"));

        var savedBook = bookRepository.save(Book.builder()
                .title(request.getTitle())
                .pageCount(request.getPageCount())
                .author(author)
                .build());
        return BookResponseDto.builder()
                .title(savedBook.getTitle())
                .pageCount(savedBook.getPageCount())
                .author(savedBook.getAuthor() == null ? null :
                        AuthorResponseDto.builder()
                                .firstName(savedBook.getAuthor().getFirstName())
                                .lastName(savedBook.getAuthor().getLastName())
                                .build())
                .build();
    }

    @MutationMapping
    public BookResponseDto updateBook(@Argument(name = "input") UpdateBookRequest request) {
        var book = bookRepository.findById(request.getId()).orElse(null);
        if (book == null) {
            throw new RuntimeException("Book not found");
        }
        book.setTitle(request.getTitle());
        book.setPageCount(request.getPageCount());
        var updatedBook = bookRepository.save(book);
        return (BookResponseDto.builder()
                .title(updatedBook.getTitle())
                .pageCount(updatedBook.getPageCount())
                .author(updatedBook.getAuthor() == null ? null :
                        AuthorResponseDto.builder()
                                .firstName(updatedBook.getAuthor().getFirstName())
                                .lastName(updatedBook.getAuthor().getLastName())
                                .build())
                .build()
        );
    }

    @MutationMapping
    public String deleteBook(@Argument(name = "input") Long id) {
        var book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            throw new RuntimeException("Book not found");
        }
        bookRepository.delete(book);
        return "Book with ID : " + id + " Title : " + book.getTitle() + " Deleted";
    }

    @QueryMapping
    public List<AuthorResponseDto> getAllAuthors() {
        return authorRepository.findAll()
                .stream()
                .map(a ->
                        AuthorResponseDto.builder()
                                .firstName(a.getFirstName())
                                .lastName(a.getLastName())
                                .build()
                )
                .toList();
    }

    @MutationMapping
    public AuthorResponseDto addAuthor(@Argument(name = "input") AddAuthorRequest request) {
        var author = new Author();
        author.setFirstName(request.getFirstName());
        author.setLastName(request.getLastName());
        var savedAuthor = authorRepository.save(author);

        return AuthorResponseDto.builder()
                .firstName(savedAuthor.getFirstName())
                .lastName(savedAuthor.getLastName())
                .build();
    }

    @MutationMapping
    public AuthorResponseDto deleteAuthor(@Argument(name = "input") Long id) {
        var author = authorRepository.findById(id).orElseThrow(()-> new RuntimeException("Author Not Found"));
        authorRepository.delete(author);
        return AuthorResponseDto.builder()
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .build();
    }
}
