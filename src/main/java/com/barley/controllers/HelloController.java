package com.barley.controllers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by vikram.gulia on 11/24/15.
 */
@RestController
public class HelloController {

    @Autowired
    SocketController socketController;

    @RequestMapping("/hello")
    String home() {
        return "Hello World!!!";
    }

    @RequestMapping("/comment/{comment}")
    void comment(@PathVariable("comment") String comment) {
        socketController.emit(comment);
    }

}