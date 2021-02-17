package uk.co.encity.notification.service;

import javax.mail.MessagingException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.util.Logger;
import reactor.util.Loggers;
import uk.co.encity.notification.events.UserCreatedEvent;
import uk.co.encity.notification.events.UserCreatedEventDeserializer;

import java.io.IOException;

@Component
public class UserCreatedHandler {
    private final Logger logger = Loggers.getLogger(getClass());


    @Value("${uk.co.encity.notification.confirm.url}")
    private String confirmURL;

    private final SMTPMailer smtpMailer;

    public UserCreatedHandler(@Autowired SMTPMailer mailer) {
        this.smtpMailer = mailer;
    }

    public void receiveUserCreatedEvent(String message) {
        logger.debug("Received <" + message + ">");
        // De-serialise the JSON into a POJO
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(UserCreatedEvent.class, new UserCreatedEventDeserializer());
        mapper.registerModule(module);
        mapper.registerModule(new JavaTimeModule()); // Needed for handling java.time.Instant objects
        UserCreatedEvent evt = null;
        try {
            evt = mapper.readValue(message, UserCreatedEvent.class);
            logger.debug(this.getClass().getName() + " de-serialisation successful");
        } catch (IOException e) {
            logger.error(this.getClass().getName() + " de-serialization error: " + e.getMessage());
        }

        // Create an email to the user

        String userId = evt.getUserId();

        String msgText = "Dear " + evt.getFirstName() + " " + evt.getLastName() + ",\n\n" +
                "Please confirm creation of your new Encity user account for tenancy: " +
                evt.getTenancyId() + ".\n\n" +
                // TODO: Change tenancy id to tenancy domain / name
                "Please note: if it remains unconfirmed, this account will automatically expire at " + "." +
                "If you were not expecting this email, please contact an Encity administrator in your organisation.\n\n" +
                "Kind regards,\n\n" +
                "Encity Customer Support";
                // TODO:
                //"  Expiry: " + evt.getExpiryTime() + "\n" +
                //"  URL: " + this.confirmURL + "/" + userId + "?action=confirm&uuid=" + evt.getConfirmUUID();

        try {
            String subject = "New Encity user created - please review";

            this.smtpMailer.sendEmail(evt.getEmailAddress(), subject, msgText);
            logger.debug("Email sent successfully to: " + evt.getEmailAddress());
        }
        catch (MessagingException mex) {
            // Prints all nested (chained) exceptions as well
            mex.printStackTrace();
        }
    }
}
