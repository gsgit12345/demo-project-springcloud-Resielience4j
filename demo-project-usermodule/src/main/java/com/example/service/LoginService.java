package com.example.service;

import com.example.entity.User;
import com.example.repository.LoginRepository;
import com.example.request.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.Optional;

@Service
public class LoginService {
    @Autowired
    LoginRepository loginRepository;


    public User createUserHandler(@RequestBody User user) {
        User userPojo = loginRepository.save(user);
        return userPojo;
    }

    public User findById(@RequestBody LoginRequest loginRequest) {
        System.out.println("user id is::"+loginRequest.getId());
        Optional<User> user = loginRepository.findById(loginRequest.getId());
        System.out.println("user  is::"+user.toString());
        return user.get();
    }
}