package uk.co.encity.notification.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import reactor.util.Logger;
import reactor.util.Loggers;
import uk.co.encity.notification.components.EmailRecipient;

@Getter @Setter
public class TenancyHmrcAuthorisationRequestedEvent {
    private final Logger logger = Loggers.getLogger(getClass());

    private String tenancyId;
    private String domain;
    private EmailRecipient authorisedContact;
    private String requestUUID;
    private String expiryTime;

    public TenancyHmrcAuthorisationRequestedEvent() {}

    public TenancyHmrcAuthorisationRequestedEvent(
            String tenancyId,
            String domain,
            EmailRecipient authorisedContact,
            String requestUUID,
            String expiryTime) {
        this.tenancyId = tenancyId;
        this.domain = domain;
        this.authorisedContact = authorisedContact;
        this.requestUUID = requestUUID;
        this.expiryTime = expiryTime;
    }
}
