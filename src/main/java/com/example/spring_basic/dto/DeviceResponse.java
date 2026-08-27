package com.example.spring_basic.dto;

public class DeviceResponse {

    private Long id;
    private String name;
    private Long userId;

    public DeviceResponse(Long id, String name, Long userId){
        this.id = id;
        this.name = name;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getUserId() {
        return userId;
    }
}
