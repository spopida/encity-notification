package uk.co.encity.notification.events;

import uk.co.encity.notification.components.EmailRecipient;

import reactor.util.Logger;
import reactor.util.Loggers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class TenancyCreatedEvent {
    private final Logger logger = Loggers.getLogger(getClass());

    private String tariff;
    private EmailRecipient authorisedContact;
    private String confirmUUID;
    private String expiryTime;

    public TenancyCreatedEvent(String tariff, EmailRecipient authContact, String confirmUUID, String creationTime) {

        this.tariff = tariff;
        this.authorisedContact = authContact;
        this.confirmUUID = confirmUUID;
        this.expiryTime = creationTime;
    }

    public String getExpiryTime() { return this.expiryTime; }
    public String getConfirmUUID() { return this.confirmUUID; }
    public String getTariff() { return this.tariff; }
    public EmailRecipient getAuthorisedContact() { return this.authorisedContact; }
}
