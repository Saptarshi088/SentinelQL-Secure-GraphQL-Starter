package com.saptarshi.DemoInterview.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorResponseDto {
    private String firstName;
    private String lastName;
}
