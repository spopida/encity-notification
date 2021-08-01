package uk.co.encity.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.util.Logger;
import reactor.util.Loggers;
import uk.co.encity.notification.events.TenancyHmrcAuthorisationRequestedEventDeserializer;
import uk.co.encity.notification.events.TenancyHmrcAuthorisationRequestedEvent;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Component
public class TenancyHmrcAuthorisationRequestedHandler {
    private final Logger logger = Loggers.getLogger(getClass());
    private final String subject = "Authorisation to access HMRC data requested  - please confirm";

    /**
     * The base URL of encity so that we know what link to put in an email
     */
    @Value("${uk.co.encity.notification.confirm.url}")
    private String authoriseURL;

    private final SMTPMailer smtpMailer;

    public TenancyHmrcAuthorisationRequestedHandler(@Autowired SMTPMailer mailer) {
        this.smtpMailer = mailer;
    }

    public void receiveMessage(String message) {
        logger.debug("Received <" + message + ">");

        // Deserialize the JSON into a POJO
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(
                TenancyHmrcAuthorisationRequestedEvent.class,
                new TenancyHmrcAuthorisationRequestedEventDeserializer());
        mapper.registerModule(module);
        mapper.registerModule(new JavaTimeModule()); // Needed for handling java.time.Instant objects

        TenancyHmrcAuthorisationRequestedEvent evt = null;
        try {
            evt = mapper.readValue(message, TenancyHmrcAuthorisationRequestedEvent.class);
            logger.debug(this.getClass().getName() + " de-serialization successful");
        } catch (IOException e) {
            logger.error(this.getClass().getName() + " de-serialization error: " + e.getMessage());
        }

        // Create an email requesting authorisation

        String tenancyId = evt.getTenancyId();

        Map contentMap = new HashMap<String, String>();
        contentMap.put("encityClientName", "GovBuddy"); // TODO: Change this to a property
        contentMap.put("tenancyDomain", evt.getDomain());
        contentMap.put("expiry", evt.getExpiryTime());

        String msgTemplate =
                "Dear " + evt.getAuthorisedContact().getFirstName() + " " + evt.getAuthorisedContact().getLastName() + "\n\n" +

                "As the ${encityClientName} Authorised Contact for the ${tenancyDomain} account, you are required to authorise ${encityClientName} to access your HMRC data. " +
                "Please click on the URL below to perform authorisation.\n\n" +

                "Please note that this request will expire at ${expiry}.\n\n" +

                "Please do not reply to this email as it is auto-generated, and replies will not be seen or actioned.\n\n" +

                "    Authorise URL: " + this.authoriseURL + "tenancy/" + tenancyId + "/authorise-hmrc?uuid=" + evt.getRequestUUID() + "\n\n" +

                "Kind regards,\n\n" +

                "${encityClientName}";

        StringSubstitutor sub = new StringSubstitutor(contentMap);
        String resolvedString = sub.replace(msgTemplate);

        try {
            String toAddr = evt.getAuthorisedContact().getEmailAddress();
            this.smtpMailer.sendEmail(toAddr, subject, resolvedString);
            logger.debug( "Email sent successfully to: " + toAddr);
        } catch (MessagingException mex) {
            logger.error("Email failed due to: " + mex.getMessage());
        }
    }
}
