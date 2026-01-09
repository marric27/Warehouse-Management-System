package com.relatech.warehouse_management_system.security.rest;


import com.relatech.warehouse_management_system.security.JwtGenerator;
import com.relatech.warehouse_management_system.security.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping()
public class AuthenticationController {
    @Autowired
    public AuthenticationManager manager;
    @Autowired
    private JwtGenerator jwtGenerator;


    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserDto user) {
        Authentication auth = manager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String token = jwtGenerator.generateToken(auth);

        return ResponseEntity.ok(Map.of("token", token));


    }


}
