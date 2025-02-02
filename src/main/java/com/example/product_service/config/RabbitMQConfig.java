package com.example.product_service.config;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public Queue lowStockEmailQueue(){
        return new Queue("lowStockQueue");
    }

    @Bean
    public Queue updateOrderQueue(){
        return new Queue("updateOrderStatusQueue");
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange("exchange");
    }



    @Bean
    public Binding bindingQueue(Queue updateOrderQueue, TopicExchange exchange) {
        return BindingBuilder.bind(updateOrderQueue).to(exchange).with("routing.key2");
    }

    @Bean
    public Binding bindingQueue2(Queue lowStockEmailQueue, TopicExchange exchange) {
        return BindingBuilder.bind(lowStockEmailQueue).to(exchange).with("routing.key5");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }

}