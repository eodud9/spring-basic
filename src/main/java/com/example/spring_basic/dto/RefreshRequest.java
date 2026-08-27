package com.example.spring_basic.dto;

public class RefreshRequest {

    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public RefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
