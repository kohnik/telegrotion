package com.fizzly.backend.config;

import com.fizzly.backend.service.brainring.BrainRingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.util.List;
import java.util.UUID;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<UUID, String> roomRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<UUID, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        return template;
    }

    @Bean
    public RedisTemplate<String, UUID> roomRedisTemplateInvert(RedisConnectionFactory factory) {
        RedisTemplate<String, UUID> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        return template;
    }

    @Bean
    public RedisTemplate<String, List<BrainRingService.BrainRingTeam>> teamRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, List<BrainRingService.BrainRingTeam>> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<String, BrainRingService.BrainRingActiveRoom> activeRoomRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, BrainRingService.BrainRingActiveRoom> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

}
