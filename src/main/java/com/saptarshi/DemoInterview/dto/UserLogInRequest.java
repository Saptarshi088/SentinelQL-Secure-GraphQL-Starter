package com.saptarshi.DemoInterview.dto;

import lombok.Data;

@Data
public class UserLogInRequest {
    private String email;
    private String password;
}
