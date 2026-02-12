package com.saptarshi.DemoInterview.dto;

import lombok.Data;

@Data
public class UpdateBookRequest {
    private Long id;
    private String title;
    private int pageCount;
}
