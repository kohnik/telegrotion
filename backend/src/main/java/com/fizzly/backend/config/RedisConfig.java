package com.fizzly.backend.config;

import com.fizzly.backend.dto.brainring.BrainRingActiveRoom;
import com.fizzly.backend.dto.brainring.BrainRingTeam;
import com.fizzly.backend.entity.BrainRingEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.util.List;
import java.util.UUID;

@Configuration
public class RedisConfig {

    @Value("${heroku.redis.host}")
    private String host;
    @Value("${heroku.redis.port}")
    private int port;
    @Value("${heroku.redis.password}")
    private String password;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setPassword(RedisPassword.of(password));

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .useSsl()
                .disablePeerVerification()
                .build();

        return new LettuceConnectionFactory(config, clientConfig);
    }

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
    public RedisTemplate<String, List<BrainRingTeam>> teamRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, List<BrainRingTeam>> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<String, BrainRingActiveRoom> activeRoomRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, BrainRingActiveRoom> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<UUID, BrainRingEvent> currentEventRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<UUID, BrainRingEvent> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<String, String> currentEventPayloadRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

}
