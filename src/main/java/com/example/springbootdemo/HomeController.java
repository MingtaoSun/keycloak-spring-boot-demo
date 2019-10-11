package com.example.springbootdemo;

import java.util.Arrays;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}
