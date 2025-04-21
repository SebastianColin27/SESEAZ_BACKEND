package com.scspd.backend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
//@CrossOrigin(origins = {"http://localhost:4200"})
@CrossOrigin(origins = {"http://seseaz-frontend.vercel.app"})
public class DemoController {

    @PostMapping(value = "demo")
    public String welcome()
    {
        return "Esta es una ruta protegida";
    }
}