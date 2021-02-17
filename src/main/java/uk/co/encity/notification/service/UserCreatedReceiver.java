package uk.co.encity.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.util.Logger;
import reactor.util.Loggers;

@Component
public class UserCreatedReceiver {

    //private static final String topicExchangeName = "encity-exchange";
    private static final String queueName = "encity-user-created";
    private final Logger logger = Loggers.getLogger(getClass());

    private UserCreatedHandler handler;

    public UserCreatedReceiver(@Autowired UserCreatedHandler handler) {
        this.handler = handler;
    }

    @Bean
    Queue userCreatedQueue() {
        return new Queue(queueName, false);
    }

    @Bean
    Binding userCreatedBinding(Queue userCreatedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(userCreatedQueue).to(exchange).with("encity.user.created");
    }

    @Bean
    SimpleMessageListenerContainer userCreatedContainer(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter userCreatedListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(userCreatedListenerAdapter);
        return container;
    }

    @Bean
    @Qualifier("user-created")
    MessageListenerAdapter userCreatedListenerAdapter(UserCreatedHandler handler) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(handler, "receiveMessage");
        adapter.setMessageConverter(new Jackson2JsonMessageConverter(new ObjectMapper()));
        return adapter;
    }
}
