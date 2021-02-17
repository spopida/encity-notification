package uk.co.encity.notification.events;

import lombok.Builder;
import lombok.Getter;
import reactor.util.Logger;
import reactor.util.Loggers;
import uk.co.encity.notification.components.EmailRecipient;

@Getter @Builder
public class UserCreatedEvent {
    private final Logger logger = Loggers.getLogger(getClass());

    private String userId;
    private String tenancyId;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private boolean isAdminUser;
    /* TODO:...
    private String confirmUUID;
    private String expiryTime;
    */
}
