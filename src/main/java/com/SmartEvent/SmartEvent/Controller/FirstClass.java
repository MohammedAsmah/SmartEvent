package com.SmartEvent.SmartEvent.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("hello")
public class FirstClass {

    @GetMapping("hh")
    public String getUsers(){
        return "Hello World!";
    }
}
