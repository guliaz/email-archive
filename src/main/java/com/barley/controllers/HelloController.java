package com.barley.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by vikram.gulia on 11/24/15.
 */
@RestController
public class HelloController {

    @RequestMapping("/hello")
    String home() {
        return "Hello World!!!";
    }
}
