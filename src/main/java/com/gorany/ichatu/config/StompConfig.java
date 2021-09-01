package com.gorany.ichatu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp/chat")
                .setAllowedOrigins("https://ichatu.ga", "https://ichatu-d9085.web.app", "http://localhost:3000")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        /*
        By default, patterns are expected to use slash (/) as the separator.
        This is a good convention in web applications and similar to HTTP URLs.
        However, if you are more used to messaging conventions, you can switch to using dot (.) as the separator.
         */
        //registry.setPathMatcher(new AntPathMatcher("."));
        registry.setApplicationDestinationPrefixes("/pub");
        registry.enableSimpleBroker("/sub");
        //registry.enableStompBrokerRelay("/queue", "/topic", "/exchange", "/amq/queue");
//                .setRelayPort(61613)
//                .setRelayHost("127.0.0.1")
//                .setClientPasscode("guest")
//                .setClientLogin("guest");;
    }

}
