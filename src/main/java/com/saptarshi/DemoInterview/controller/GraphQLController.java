package com.saptarshi.DemoInterview.controller;

import com.saptarshi.DemoInterview.dto.AddBookRequest;
import com.saptarshi.DemoInterview.dto.BookResponseDto;
import com.saptarshi.DemoInterview.dto.UpdateBookRequest;
import com.saptarshi.DemoInterview.entity.Book;
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

    @QueryMapping
    public List<BookResponseDto> getAllBooks() {
        return bookRepository.findAllWithEagerRelationships()
                .stream()
                .map(b ->
                        BookResponseDto
                                .builder()
                                .title(b.getTitle())
                                .pageCount(b.getPageCount())
                                .authorFirstName(b.getAuthor().getFirstName())
                                .authorLastName(b.getAuthor().getLastName())
                                .build()

                )
                .toList();
    }

    @MutationMapping
    public BookResponseDto addBook(@Argument(name = "input") AddBookRequest request){

        var savedBook = bookRepository.save(Book.builder()
                        .title(request.getTitle())
                        .pageCount(request.getPageCount())
                        .build());
        return BookResponseDto.builder()
                .title(savedBook.getTitle())
                .pageCount(savedBook.getPageCount())
                .build();
    }

    @MutationMapping
    public BookResponseDto updateBook(@Argument(name = "input") UpdateBookRequest request){
        var book = bookRepository.findById(request.getId()).orElse(null);
        if(book == null){
            throw new RuntimeException("Book not found");
        }
        book.setTitle(request.getTitle());
        book.setPageCount(request.getPageCount());
        var updatedBook = bookRepository.save(book);
        return (BookResponseDto.builder()
                .title(updatedBook.getTitle())
                .pageCount(updatedBook.getPageCount())
                .build()
            );
    }

    @MutationMapping
    public String deleteBook(@Argument(name = "input") Long id){
        var book = bookRepository.findById(id).orElse(null);
        if(book == null){
            throw new RuntimeException("Book not found");
        }
        bookRepository.delete(book);
        return "Book with ID : " + id + " Title : " + book.getTitle() + " Deleted";
    }
}
