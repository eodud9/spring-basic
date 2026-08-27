package com.example.spring_basic.service;

import com.example.spring_basic.dto.*;
import com.example.spring_basic.entity.User;
import com.example.spring_basic.exception.UserNotFoundException;
import com.example.spring_basic.jwt.JwtTokenProvider;
import com.example.spring_basic.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public UserResponse createUser(UserCreateRequest request){
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getName(), request.getEmail(), encodedPassword);

        User createdUser = userRepository.save(user);

        return new UserResponse(createdUser.getId(),createdUser.getName(),createdUser.getEmail());
    }

    public UserResponse getUser(Long id){

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return new UserResponse(user.getId(),user.getName(), user.getEmail());
    }

    public List<UserResponse> getUsers(){

        return userRepository.findAll().stream().map(user -> new UserResponse(
                user.getId(),user.getName(),user.getEmail()
        )).toList();
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request, String email) throws AccessDeniedException {

        User targetUser = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException(id));
        User loginUser = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if(!targetUser.getId().equals(loginUser.getId())){
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        targetUser.update(request.getName(), request.getEmail());

        return new UserResponse(targetUser.getId(), targetUser.getName(),targetUser.getEmail());
    }

    public void deleteUser(Long id){
        User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException(id));
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public void testNPlusOne(){
        List<User> users = userRepository.findAllWithDevices();

        for (User user: users){
            System.out.println(user.getName() + " " +user.getDevices().size());
        }
    }
}
