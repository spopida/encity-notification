package uk.co.encity.notification.service;

import lombok.AllArgsConstructor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.util.Logger;
import reactor.util.Loggers;

@Component
@AllArgsConstructor
public class HmrcVatAuthzRequestedReceiver {

    private static final String queueName = "encity-tenancy-hmrc_vat_authz_requested";

    private final Logger logger = Loggers.getLogger(getClass());

    private HmrcVatAuthzRequestedHandler handler;

    @Bean
    Queue hmrcVatAuthzRequestedQueue() { return new Queue(queueName, false); }

    @Bean
    Binding hmrcVatAuthzRequestedBinding(Queue hmrcVatAuthzRequestedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(hmrcVatAuthzRequestedQueue).to(exchange).with("encity.tenancy.hmrc_vat_authz_requested");
    }

    @Bean
    SimpleMessageListenerContainer hmrcVatAuthzRequestedContainer(
            ConnectionFactory connectionFactory,
            MessageListenerAdapter hmrcVatAuthzRequestedListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(hmrcVatAuthzRequestedListenerAdapter);
        return container;
    }

    @Bean
    @Qualifier("hmrc-vat-authz-requested")
    MessageListenerAdapter hmrcVatAuthzRequestedListenerAdapter(
            HmrcVatAuthzRequestedHandler handler) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(handler, "receiveMessage");
        adapter.setMessageConverter(new Jackson2JsonMessageConverter(new ObjectMapper()));
        return adapter;
    }
}
