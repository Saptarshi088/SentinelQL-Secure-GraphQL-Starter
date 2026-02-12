package com.saptarshi.DemoInterview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AddBookRequest {
    private String title;
    private Integer pageCount;
}
