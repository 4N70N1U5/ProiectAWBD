package com.antonio.skybase.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/web")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/")
    public String redirectToWeb() {
        return "redirect:/web";
    }
}
