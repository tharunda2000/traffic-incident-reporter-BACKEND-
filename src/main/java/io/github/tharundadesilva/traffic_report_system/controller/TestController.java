package io.github.tharundadesilva.traffic_report_system.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class TestController {


    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public String hello(){
        return "hello";
    }
}
