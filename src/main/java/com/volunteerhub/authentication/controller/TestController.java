package com.volunteerhub.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/hello")
    public ResponseEntity<?> hello() {
        Map<String, String> map = new HashMap<>();
        map.put("message", "Hello World");
        map.put("time", System.currentTimeMillis() + "");
        return ResponseEntity.ok(map);
    }

    @GetMapping("/hello1")
    public ResponseEntity<?> hello1() {
        Map<String, String> map = new HashMap<>();
        map.put("message", "Hello World");
        map.put("time", System.currentTimeMillis() + "");
        return ResponseEntity.ok(map);
    }
}
