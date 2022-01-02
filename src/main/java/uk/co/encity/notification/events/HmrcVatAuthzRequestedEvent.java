package uk.co.encity.notification.events;

import lombok.Getter;
import lombok.Setter;
import reactor.util.Logger;
import reactor.util.Loggers;

@Getter @Setter
public class HmrcVatAuthzRequestedEvent {
    private final Logger logger = Loggers.getLogger(getClass());

    private String tenancyId;
    private String requestUUID;
    private String companyNumber;
    private String companyName;
    private String domain;
    private String contactEmail;
    private String expiry;

    public HmrcVatAuthzRequestedEvent() {}

    public HmrcVatAuthzRequestedEvent(
            String tenancyId,
            String requestUUID,
            String companyNumber,
            String companyName,
            String domain,
            String contactEmail,
            String expiry
    ) {
        this.tenancyId = tenancyId;
        this.requestUUID = requestUUID;
        this.companyNumber = companyNumber;
        this.companyName = companyName;
        this.domain = domain;
        this.contactEmail = contactEmail;
        this.expiry = expiry;
    }

}
