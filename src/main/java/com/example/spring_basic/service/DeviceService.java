package com.example.spring_basic.service;

import com.example.spring_basic.dto.DeviceCreateRequest;
import com.example.spring_basic.dto.DeviceResponse;
import com.example.spring_basic.entity.Device;
import com.example.spring_basic.entity.User;
import com.example.spring_basic.repository.DeviceRepository;
import com.example.spring_basic.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public DeviceService(DeviceRepository deviceRepository, UserRepository userRepository){
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

    public DeviceResponse createDevice(DeviceCreateRequest request){
        User user = userRepository.findById(request.getUserId()).orElseThrow();
        Device device = new Device(request.getName(), user);

        Device savedDevice = deviceRepository.save(device);

        return new DeviceResponse(savedDevice.getId(), savedDevice.getName(), savedDevice.getUser().getId());
    }

    public List<DeviceResponse> getDevicesByUser(Long userId){
        return deviceRepository.findByUserId(userId)
                .stream()
                .map(device -> new DeviceResponse(
                        device.getId(),
                        device.getName(),
                        device.getUser().getId()
                )).toList();
    }
}
