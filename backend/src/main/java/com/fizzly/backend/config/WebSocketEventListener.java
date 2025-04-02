//package com.fizzly.backend.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.messaging.SessionSubscribeEvent;
//
//@Component
//public class WebSocketEventListener implements ApplicationListener<SessionSubscribeEvent> {
//
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//    public void onApplicationEvent(SessionSubscribeEvent event) {
//        System.out.println("event happing");
//        this.messagingTemplate.convertAndSend("/topic/topic/session", event.getSource());
//
//    }
//}
