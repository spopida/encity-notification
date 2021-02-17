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

        return UserCreatedEvent.builder()
                .userId(node.get("userId").asText())
                .tenancyId(node.get("tenancyId").asText())
                .firstName(node.get("firstName").asText())
                .lastName(node.get("lastName").asText())
                .emailAddress(node.get("emailAddress").asText())
                .isAdminUser(node.get("isAdminUser").asBoolean())
            .build();
    }
}
