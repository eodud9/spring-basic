package com.example.spring_basic.controller;

import com.example.spring_basic.dto.UserCreateRequest;
import com.example.spring_basic.dto.UserResponse;
import com.example.spring_basic.dto.UserUpdateRequest;
import com.example.spring_basic.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/users/{id}")
    public UserResponse getUser(@PathVariable Long id){
        return userService.getUser(id);
    }

    @GetMapping("/users")
    public List<UserResponse> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/users/n-plus-one")
    public void testNPlusOne(){
        userService.testNPlusOne();
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request){
        UserResponse response = userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody UserUpdateRequest request,
                                                   Authentication authentication) throws AccessDeniedException {
        System.out.println(authentication.getName());
        UserResponse response = userService.updateUser(id, request, authentication.getName());
        return ResponseEntity
                .ok()
                .body(response);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/test")
    public String adminTest(){
        return "ADMIN ACCESS SUCCESS";
    }


}
