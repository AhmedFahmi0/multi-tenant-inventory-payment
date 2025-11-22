package com.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("This is a public endpoint");
    }

    @PostMapping("/public/post")
    public ResponseEntity<String> publicPostEndpoint(@RequestBody String message) {
        return ResponseEntity.ok("Received: " + message);
    }
}
