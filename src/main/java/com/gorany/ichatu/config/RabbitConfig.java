package com.gorany.ichatu.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    @Value("${spring.rabbitmq.host}")
    private String RABBIT_HOST;
    @Value("${spring.rabbitmq.port}")
    private Integer RABBIT_PORT;
    @Value("${spring.rabbitmq.username}")
    private String RABBIT_USERNAME;
    @Value("${spring.rabbitmq.password}")
    private String RABBIT_PASSWORD;
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

    /* messageConverter??? ?????????????????? ?????? ?????? Bean ?????? ?????? */
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

        factory.setHost(RABBIT_HOST);
        factory.setPort(RABBIT_PORT);
        factory.setUsername(RABBIT_USERNAME);
        factory.setPassword(RABBIT_PASSWORD);
        factory.setVirtualHost("myVirtual");
        return factory;
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setMaxConcurrentConsumers(1);      // ?????? ????????? ??? ??????.. ?????? ???????????? ????????????.
        container.setReceiveTimeout(3000L);         // ????????? ?????? ??? ???????????? ??? (ms)
        container.setRecoveryInterval(3000L);        // ????????? ???????????? ??? Recover ????????? ?????? ????????? ????????? ?????? term (ms)
        container.setQueueNames(CHAT_QUEUE_NAME, COMMENT_QUEUE_NAME);
        return container;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(){
        //LocalDateTime serializable??? ??????
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