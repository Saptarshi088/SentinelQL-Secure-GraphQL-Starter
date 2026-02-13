package com.saptarshi.DemoInterview.dto;

import lombok.Data;

@Data
public class AddBookRequest {
    private String title;
    private Integer pageCount;
    private Long authorId;
}
