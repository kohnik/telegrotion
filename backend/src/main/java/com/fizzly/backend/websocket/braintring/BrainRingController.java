package com.fizzly.backend.websocket.braintring;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class BrainRingController {

    private final SimpMessagingTemplate messagingTemplate;
}
