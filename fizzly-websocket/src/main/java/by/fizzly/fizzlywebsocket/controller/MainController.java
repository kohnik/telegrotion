package by.fizzly.fizzlywebsocket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping
    public String mainPage() {
        return "Hello from Fizzly websocket service!";
    }
}
