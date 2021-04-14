package uk.co.encity.notification.events;

import lombok.Builder;
import lombok.Getter;
import reactor.util.Logger;
import reactor.util.Loggers;
import uk.co.encity.notification.components.EmailRecipient;

@Getter @Builder
public class UserCreatedEvent {
    private final Logger logger = Loggers.getLogger(getClass());

    private final String userId;
    private final String tenancyId;
    private final String firstName;
    private final String lastName;
    private final String emailAddress;
    private final boolean isAdminUser;
    private final String domain;
    private final String confirmUUID;
    private final String expiryTime;
    private final String eventTime;
}
