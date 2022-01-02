package uk.co.encity.notification.events;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;

public class HmrcVatAuthzRequestedEventDeserializer extends StdDeserializer<HmrcVatAuthzRequestedEvent> {

    public HmrcVatAuthzRequestedEventDeserializer() { this(null); }
    public HmrcVatAuthzRequestedEventDeserializer(Class<?> valueClass) { super(valueClass); }

    @Override
    public HmrcVatAuthzRequestedEvent deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String tenancyId = node.get("tenancyId").asText();
        String requestUUID = node.get("requestUUID").asText();
        String companyNumber = node.get("companyNumber").asText();
        String companyName = node.get("companyName").asText();
        String domain = node.get("domain").asText();
        String contactEmail = node.get("contactEmail").asText();

        JsonNode expTime = node.get("expiry");
        long sec = expTime.get("epochSecond").asLong();
        long nan = expTime.get("nano").asLong();

        Instant i = Instant.ofEpochSecond(sec, nan);
        String expiry = i.toString();

        return new HmrcVatAuthzRequestedEvent(tenancyId, requestUUID, companyNumber, companyName, domain, contactEmail, expiry);
    }
}
