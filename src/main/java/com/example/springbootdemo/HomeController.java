package com.example.springbootdemo;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {
    
    @GetMapping(path = "/")
    public String home(Model model){
        return "home";
    }
    
    @GetMapping(path = "/products")
    public String products(Model model){
        model.addAttribute("products", Arrays.asList("Apple", "Banana"));
        return "products";
    }
    
    @PostMapping(path = "/k_logout")
    public String kLogout(HttpServletRequest request) {
        return "home";
    }
}
