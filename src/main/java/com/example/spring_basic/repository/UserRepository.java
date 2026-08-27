package com.example.spring_basic.repository;

import com.example.spring_basic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select distinct u from User u join fetch u.devices")
    List<User> findAllWithDevices();
    Optional<User> findByEmail(String email);
}
