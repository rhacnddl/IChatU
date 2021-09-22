package com.gorany.ichatu.config;

import com.gorany.ichatu.storage.CheckStorage;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

import javax.annotation.PostConstruct;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {

//    private final static CheckStorage checkStorage = CheckStorage.getInstance();
//    private Map<Long, Long> checkMap;
//
//    @PostConstruct
//    private void init(){
//        this.checkMap = checkStorage.getCheckMap();
//    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/stomp/chat")
                .setAllowedOrigins("https://ichatu.ga", "http://localhost:3000")
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

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
            @Override
            public WebSocketHandler decorate(WebSocketHandler handler) {
                return new WebSocketHandlerDecorator(handler) {

                    @Override
                    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

                    }
                };
            }
        });
    }
}
