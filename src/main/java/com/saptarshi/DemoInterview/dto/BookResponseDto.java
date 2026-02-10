package com.saptarshi.DemoInterview.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponseDto {
    private String title;
    private Integer pageCount;
}
