package uk.co.encity.notification.events;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import uk.co.encity.notification.components.EmailRecipient;

import java.io.IOException;
import java.time.Instant;

public class TenancyCreatedEventDeserializer extends StdDeserializer<TenancyCreatedEvent> {
    public TenancyCreatedEventDeserializer() {
        this(null);
    }

    public TenancyCreatedEventDeserializer(Class<?> valueClass) {
        super(valueClass);
    }

    @Override
    public TenancyCreatedEvent deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        JsonNode node = jp.getCodec().readTree(jp);
        String tenancyId = node.get("tenancyId").asText();
        String tariff = node.get("tariff").asText();
        EmailRecipient authContact = this.deserializeContact(node.get("authorisedContact"));
        String confirmUUID = node.get("confirmUUID").asText();

        JsonNode expTime = node.get("expiryTime");
        long sec = expTime.get("epochSecond").asLong();
        long nan = expTime.get("nano").asLong();

        Instant i = Instant.ofEpochSecond(sec, nan);
        String expiry = i.toString();

        return new TenancyCreatedEvent(tenancyId, tariff, authContact, confirmUUID, expiry);
    }

    private EmailRecipient deserializeContact(JsonNode node) {
        String first = node.get("firstName").asText();
        String last = node.get("lastName").asText();
        String email = node.get("emailAddress").asText();

        return new EmailRecipient(first, last, email);
    }

}
