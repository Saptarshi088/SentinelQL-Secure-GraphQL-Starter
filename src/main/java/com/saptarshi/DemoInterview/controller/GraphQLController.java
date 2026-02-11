package com.saptarshi.DemoInterview.controller;

import com.saptarshi.DemoInterview.dto.BookResponseDto;
import com.saptarshi.DemoInterview.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        return bookRepository.findAll()
                .stream()
                .map(b ->
                        BookResponseDto
                                .builder()
                                .title(b.getTitle())
                                .pageCount(b.getPageCount())
                                .build()

                )
                .toList();
    }
}
