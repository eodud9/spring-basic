package com.example.spring_basic.entity;

import jakarta.persistence.*;

@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @ManyToOne
    private User user;

    protected RefreshToken(){}

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public RefreshToken(String token, User user){
        this.token = token;
        this.user = user;

    }
}
