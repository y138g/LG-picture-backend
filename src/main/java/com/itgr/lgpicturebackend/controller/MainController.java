package com.itgr.lgpicturebackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class MainController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
