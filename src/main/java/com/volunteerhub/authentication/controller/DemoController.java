package com.volunteerhub.authentication.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/demo")
@AllArgsConstructor
public class DemoController {

    @GetMapping
    public ResponseEntity<?> getDemo() {
        return ResponseEntity.ok(Map.of("Hello", "World"));
    }
}
