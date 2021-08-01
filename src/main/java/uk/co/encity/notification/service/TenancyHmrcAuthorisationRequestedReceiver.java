package uk.co.encity.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import reactor.util.Logger;
import reactor.util.Loggers;

@Component
@AllArgsConstructor
public class TenancyHmrcAuthorisationRequestedReceiver {

    // TODO: figure out how this queue is published to, given that there is no reference
    // TODO: to it in the publishing microservice!
    private static final String queueName = "encity-tenancy-hmrc_authorisation_requested";
    private final Logger logger = Loggers.getLogger(getClass());

    private TenancyHmrcAuthorisationRequestedHandler handler;

    @Bean
    Queue tenancyHmrcAuthorisationRequestedQueue() { return new Queue(queueName, false); }

    @Bean
    Binding tenancyHmrcAuthorisationRequestedBinding(Queue tenancyHmrcAuthorisationRequestedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(tenancyHmrcAuthorisationRequestedQueue).to(exchange).with("encity.tenancy.hmrc_authorisation_requested");
    }

    @Bean
    SimpleMessageListenerContainer tenancyHmrcAuthorisationRequestedContainer(
            ConnectionFactory connectionFactory,
            MessageListenerAdapter tenancyHmrcAuthorisationRequestedListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(tenancyHmrcAuthorisationRequestedListenerAdapter);
        return container;
    }

    @Bean
    @Qualifier("hmrc-authorisation-requested")
    MessageListenerAdapter tenancyHmrcAuthorisationRequestedListenerAdapter(
            TenancyHmrcAuthorisationRequestedHandler handler) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(handler, "receiveMessage");
        adapter.setMessageConverter(new Jackson2JsonMessageConverter(new ObjectMapper()));
        return adapter;
    }
}
