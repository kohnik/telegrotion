package by.fizzly.fizzlywebsocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FizzlyWebsocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(FizzlyWebsocketApplication.class, args);
    }

}
