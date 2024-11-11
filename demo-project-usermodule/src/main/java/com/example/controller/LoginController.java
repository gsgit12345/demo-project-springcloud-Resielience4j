package com.example.controller;

import com.example.entity.User;
import com.example.repository.LoginRepository;
import com.example.request.LoginRequest;
import com.example.service.LoginService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @Autowired
    LoginService loginService;
    @Autowired
    PasswordEncoder passwordEncoder;
    private final LoginService countriesService;

    public LoginController(LoginService countriesService) {
        this.countriesService = countriesService;
    }

    @PostMapping("/signup")
    @CircuitBreaker(name = "countriesService", fallbackMethod = "fallbackRegisterUser")
    public ResponseEntity<String> createUserHandler(@RequestBody User user) throws Exception {
        String email = user.getEmail();
        String password = user.getPassword();
        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setPassword(passwordEncoder.encode(password));
        User savedUser = loginService.createUserHandler(createdUser);
        if(!email.contains("@"))
        {
            System.out.println("email check ");
            throw new Exception("service call fail");
        }
        return new
                ResponseEntity<>("User Registration Sucess", HttpStatus.OK);

    }
    @PostMapping("/signin")
    @CircuitBreaker(name = "countriesService", fallbackMethod = "fallbackLoginUser")
    public ResponseEntity<String> sign(@RequestBody LoginRequest loginRequest) {
        // String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        // Retrieve the user from the database based on the email
        User user = loginService.findById(loginRequest);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
        // Verify the password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
        // You can generate a token here and return it as part of the response
        // For simplicity, let's just return a success message for now
        return ResponseEntity.ok("Login successful");
    }

    // Fallback method for user registration
    public ResponseEntity<String> fallbackRegisterUser(User user,Throwable exception) {
        // Lothe error or perform any necessary handling
        System.err.println("User registration failed. Fallback method called.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("User Registration Failed. Please try again later.");
    }
    // Fallback method for user login
    public ResponseEntity<String> fallbackLoginUser(LoginRequest loginRequest) {
        // Log the error or perform any necessary handling
        System.err.println("User login failed. Fallback method called.");
        return
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("User Login Failed. Please try again later.");
    }

}
