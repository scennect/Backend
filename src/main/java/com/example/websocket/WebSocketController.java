//package com.example.websocket;
//
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Controller;
//
//@Controller
//public class WebSocketController {
//
//    @MessageMapping("/sendMessage")
//    @SendTo("/topic/messages")
//    public String broadcastMessage(String message) {
//        return message;
//    }
//    @MessageMapping("/updateCoordinates")
//    @SendTo("/topic/coordinates")
//    public String updateCoordinates(String message) {
//        return message;
//    }
//}