package com.example.spring_basic.entity;

import jakarta.persistence.*;

@Entity
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    private User user;

    protected Device(){}

    public Device(String name, User user){
        this.name = name;
        this.user = user;
    }

    public Long getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public User getUser() {
        return user;
    }
}
