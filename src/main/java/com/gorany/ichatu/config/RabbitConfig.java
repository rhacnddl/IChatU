package com.gorany.ichatu.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@EnableRabbit
public class RabbitConfig {

    @Value("${chat.queue.name}")
    private String CHAT_QUEUE_NAME;
    @Value("${comment.queue.name}")
    private String COMMENT_QUEUE_NAME;
    @Value("${chat.routing.key}")
    private String CHAT_ROUTING_KEY;
    @Value("${comment.routing.key}")
    private String COMMENT_ROUTING_KEY;
    @Value("${exchange.name}")
    private String EXCHANGE_NAME;

    /*
    public Queue(String name, boolean durable) {
        this(name, durable, false, false, null);
    }
    */
//    @Bean
//    public Queue queue(){ return new Queue(CHAT_QUEUE_NAME, true); }
//
//    @Bean
//    public TopicExchange exchange(){ return new TopicExchange(CHAT_EXCHANGE_NAME); }
//
//    @Bean
//    public Binding binding(Queue queue, TopicExchange exchange) {
//        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
//    }

    @Bean
    public Declarables declarables(){
        Queue chatQueue = new Queue(CHAT_QUEUE_NAME, false);
        Queue commentQueue = new Queue(COMMENT_QUEUE_NAME, false);

        TopicExchange topicExchange = new TopicExchange(EXCHANGE_NAME);
        return new Declarables(
                chatQueue, commentQueue, topicExchange
                , BindingBuilder.bind(chatQueue).to(topicExchange).with(CHAT_ROUTING_KEY)
                , BindingBuilder.bind(commentQueue).to(topicExchange).with(COMMENT_ROUTING_KEY)
        );
    }

    /* messageConverter를 커스터마이징 하기 위해 Bean 새로 등록 */
    @Bean
    public RabbitTemplate rabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

//    @Bean
//    public SimpleMessageListenerContainer container(){
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory());
//        container.setQueueNames(CHAT_QUEUE_NAME);
//        container.setMessageListener(null);
//        return container;
//    }

    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory factory = new CachingConnectionFactory();

        factory.setHost("34.64.188.95");
        factory.setPort(5672);
        factory.setUsername("gorany");
        factory.setPassword("gorany!");
        factory.setVirtualHost("/");
        return factory;
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setMaxConcurrentConsumers(1);      // 최대 컨슈머 수 인데.. 어떤 기준인지 모르겠다.
        container.setReceiveTimeout(3000L);         // 메시지 받을 때 타임아웃 값 (ms)
        container.setRecoveryInterval(3000L);        // 연결이 끊어졌을 시 Recover 시도를 어느 주기로 할지에 대한 term (ms)
        container.setQueueNames(CHAT_QUEUE_NAME, COMMENT_QUEUE_NAME);
        return container;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(){
        //LocalDateTime serializable을 위해
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        objectMapper.registerModule(dateTimeModule());

        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);

        return converter;
    }

    @Bean
    public Module dateTimeModule(){
        return new JavaTimeModule();
    }

}