package uk.co.encity.notification.components;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

@Getter
public class EmailRecipient {

    public static EmailRecipient deserialize(JsonNode node) {
        String first = node.get("firstName").asText();
        String last = node.get("lastName").asText();
        String email = node.get("emailAddress").asText();

        return new EmailRecipient(first, last, email);
    }

    private String firstName;
    private String lastName;
    private String emailAddress;

    public EmailRecipient(String firstName, String lastName, String emailAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
    }

}
