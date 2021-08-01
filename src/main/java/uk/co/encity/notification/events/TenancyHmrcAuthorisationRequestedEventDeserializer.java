package uk.co.encity.notification.events;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import uk.co.encity.notification.components.EmailRecipient;

import java.io.IOException;
import java.time.Instant;

public class TenancyHmrcAuthorisationRequestedEventDeserializer extends StdDeserializer<TenancyHmrcAuthorisationRequestedEvent> {

    public TenancyHmrcAuthorisationRequestedEventDeserializer() { this(null); }

    public TenancyHmrcAuthorisationRequestedEventDeserializer(Class<?> valueClass) { super(valueClass); }

    @Override
    public TenancyHmrcAuthorisationRequestedEvent deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String tenancyId = node.get("tenancyId").asText();
        String domain = node.get("domain").asText();

        EmailRecipient authContact = EmailRecipient.deserialize(node.get("authorisedContact"));
        String requestUUID = node.get("requestUUID").asText();

        JsonNode expTime = node.get("expiryTime");
        long sec = expTime.get("epochSecond").asLong();
        long nan = expTime.get("nano").asLong();

        Instant i = Instant.ofEpochSecond(sec, nan);
        String expiry = i.toString();

        return new TenancyHmrcAuthorisationRequestedEvent(tenancyId, domain, authContact, requestUUID, expiry);
    }
}
