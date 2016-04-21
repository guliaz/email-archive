package com.barley.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class SocketController {

    private List<SseEmitter> sseEmitters = Collections.synchronizedList(new ArrayList<>());

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        System.out.println("!!!!!!!!!!!Message received:!!!!!!!!! " + message);
        Thread.sleep(3000); // simulated delay
        System.out.println("Sending message back!!!!");
        return new Greeting("Hello, " + message.getName() + "!");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/emit")
    public SseEmitter emitter() {
        final SseEmitter emitter = new SseEmitter();
        synchronized (this.sseEmitters) {
            this.sseEmitters.add(emitter);
            Runnable runnable = () -> {
                synchronized (this.sseEmitters) {
                    this.sseEmitters.remove(emitter);
                }
            };
            emitter.onCompletion(runnable);
            emitter.onTimeout(runnable);
        }
        return emitter;
    }

    public void emit(String message) {
        synchronized (this.sseEmitters) {
            for (SseEmitter sseEmitter : this.sseEmitters) {
                // Servlet containers don't always detect ghost connection, so we must catch exceptions ...
                try {
                    sseEmitter.send(message);
                } catch (Exception e) {
                }
            }
        }
    }
}
