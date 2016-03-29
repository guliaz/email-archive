package com.barley.socket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class MyHandler extends TextWebSocketHandler {

    private AtomicLong atomicLong = new AtomicLong();
    private static boolean keepRunning = true;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        System.out.println("Connection established");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        if (message.getPayload().equalsIgnoreCase("STOP")) {
            keepRunning = false;
        } else {
            session.sendMessage(new TextMessage("returning messages back : " + message.getPayload()));
            sendMessages(session, 10);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        System.out.println("Connection closed");
    }

    private void sendMessages(WebSocketSession session, int times) {
        Thread thread = new Thread(() -> {
            int i = 0;
            while (keepRunning && i < times) {
                try {
                    Thread.sleep(10000);
                    session.sendMessage(new TextMessage("Status update : time elapsed - " + atomicLong.incrementAndGet()));
                } catch (Exception e) {

                }
                i++;
            }
        });
        thread.start();
    }
}

