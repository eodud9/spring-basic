package com.example.spring_basic.controller;

import com.example.spring_basic.dto.DeviceCreateRequest;
import com.example.spring_basic.dto.DeviceResponse;
import com.example.spring_basic.entity.Device;
import com.example.spring_basic.service.DeviceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService){
        this.deviceService = deviceService;
    }

    @PostMapping("/devices")
    public ResponseEntity<DeviceResponse> createDevice(@RequestBody DeviceCreateRequest request){
        DeviceResponse response = deviceService.createDevice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/users/{userId}/devices")
    public List<DeviceResponse> getDevicesByUser(@PathVariable Long userId){
        return deviceService.getDevicesByUser(userId);
    }
}
