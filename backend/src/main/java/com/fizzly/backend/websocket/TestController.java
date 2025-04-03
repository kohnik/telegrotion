package com.fizzly.backend.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class TestController {

    @MessageMapping("/send-message")
    public void test(@Payload String message, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("message = " + message);
//        System.out.println("message.getPayload() = " + ((String)message.getPayload()));
//        System.out.println("message.getHeaders() = " + message.getHeaders());
//        System.out.println("headerAccessor.getDestination() = " + headerAccessor.getDestination());
//        System.out.println("headerAccessor.getSessionId() = " + headerAccessor.getSessionId());
//        System.out.println("headerAccessor.getSessionAttributes() = " + headerAccessor.getSessionAttributes());
    }
}
