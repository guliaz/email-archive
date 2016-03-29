package com.barley.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class SocketController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        System.out.println("!!!!!!!!!!!Message received:!!!!!!!!! " + message);
        Thread.sleep(3000); // simulated delay
        System.out.println("Sending message back!!!!");
        return new Greeting("Hello, " + message.getName() + "!");
    }

}
