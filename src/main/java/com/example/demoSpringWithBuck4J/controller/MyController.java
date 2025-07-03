package com.example.demoSpringWithBuck4J.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class MyController {

    @GetMapping("/hello-world")
    public ResponseEntity<String> helloWord() {
        return ResponseEntity.ok("test");
    }
}
