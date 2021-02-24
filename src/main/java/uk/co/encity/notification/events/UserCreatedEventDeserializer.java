package uk.co.encity.notification.events;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import uk.co.encity.notification.components.EmailRecipient;

import java.io.IOException;
import java.time.Instant;

public class UserCreatedEventDeserializer extends StdDeserializer<UserCreatedEvent> {
    public UserCreatedEventDeserializer() {
        this(null);
    }

    public UserCreatedEventDeserializer(Class<?> valueClass) {
        super(valueClass);
    }

    @Override
    public UserCreatedEvent deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        JsonNode node = jp.getCodec().readTree(jp);
        JsonNode userNode = node.get("user");
        JsonNode eventNode = node.get("event");

        return UserCreatedEvent.builder()
                .userId(userNode.get("userId").asText())
                .tenancyId(userNode.get("tenancyId").asText())
                .firstName(userNode.get("firstName").asText())
                .lastName(userNode.get("lastName").asText())
                .emailAddress(userNode.get("emailAddress").asText())
                .isAdminUser(userNode.get("adminUser").asBoolean())
                .confirmUUID(userNode.get("confirmUUID").asText())
                .domain(userNode.get("domain").asText())
                .expiryTime(userNode.get("expiryTime").asText())
                .eventTime(eventNode.get("eventTime").asText())
            .build();
    }
}
