package com.example.spring_basic.dto;

public class DeviceCreateRequest {

    private String name;
    private Long userId;

    protected DeviceCreateRequest(){}

    public DeviceCreateRequest(String name, Long userId){
        this.name = name;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public Long getUserId() {
        return userId;
    }
}
