package com.example.spring_basic.dto;

public class LogoutRequest {

    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public LogoutRequest(String refreshToken){
        this.refreshToken = refreshToken;
    }
}
