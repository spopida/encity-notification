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
    private EmailRecipient billingContact;
    private UUID confirmUUID;
    private Instant creationTime;

    public TenancyCreatedEvent(String tariff, EmailRecipient authContact, EmailRecipient billingContact, UUID confirmUUID, Instant creationTime) {

        this.tariff = tariff;
        this.authorisedContact = authContact;
        this.billingContact = billingContact;
        this.confirmUUID = confirmUUID;
        this.creationTime = creationTime;
    }

    // TODO: don't hard code 1 hour...
    public Instant getExpiryTime() { return this.creationTime.plus(1, ChronoUnit.HOURS); }
    public Instant getCreationTime() { return this.creationTime; }
    public UUID getConfirmUUID() { return this.confirmUUID; }
    public String getTariff() { return this.tariff; }
    public EmailRecipient getAuthorisedContact() { return this.authorisedContact; }
    public EmailRecipient getBillingContact() { return this.billingContact; }
}
