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
import uk.co.encity.notification.events.HmrcVatAuthzRequestedEvent;
import uk.co.encity.notification.events.HmrcVatAuthzRequestedEventDeserializer;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class HmrcVatAuthzRequestedHandler {
    private final Logger logger = Loggers.getLogger(getClass());
    private final String subject = "Authorisation to access HMRC data requested  - please respond";

    /**
     * The base URL of encity so that we know what link to put in an email
     */
    @Value("${uk.co.encity.notification.confirm.url}")
    private String authoriseUrlRoot;

    private final SMTPMailer smtpMailer;

    public HmrcVatAuthzRequestedHandler(@Autowired SMTPMailer mailer) { this.smtpMailer = mailer; }

    public void receiveMessage(String message) {
        logger.debug("Received <" + message + ">");

        // Deserialize the JSON into a POJO
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(
                HmrcVatAuthzRequestedEvent.class,
                new HmrcVatAuthzRequestedEventDeserializer());
        mapper.registerModule(module);
        mapper.registerModule(new JavaTimeModule()); // Needed for handling java.time.Instant objects

        HmrcVatAuthzRequestedEvent evt = null;
        try {
            evt = mapper.readValue(message, HmrcVatAuthzRequestedEvent.class);
            logger.debug(this.getClass().getName() + " de-serialization successful");
        } catch (IOException e) {
            logger.error(this.getClass().getName() + " de-serialization error: " + e.getMessage());
        }

        // Create an email requesting authorisation

        String tenancyId = evt.getTenancyId();

        Map contentMap = new HashMap<String, String>();
        contentMap.put("encityClientName", "GovBuddy"); // TODO: Change this to a property
        contentMap.put("companyName", evt.getCompanyName());
        contentMap.put("tenancyDomain", evt.getDomain());
        contentMap.put("expiry", evt.getExpiry());
        contentMap.put("authoriseUrl", this.authoriseUrlRoot + "tenancy/" + tenancyId + "/authorise-hmrc-vat?uuid=" + evt.getRequestUUID());

        String msgTemplate =
                "Dear " + evt.getContactEmail() + "\n\n" +

                        "As the recorded contact for ${companyName} in the ${encityClientName} account for ${tenancyDomain},\n" +
                        "you are requested authorise access to your HMRC data. This will enable users of the account to \n" +
                        "retrieve and update the HMRC records of ${companyName} (subject to their specific permissions in ${encityClientName})  .\n\n" +

                        "Please click on the URL below to authorise or reject this request.\n\n" +

                        "Please note that this request will expire at ${expiry}.\n\n" +

                        "Please IGNORE this request if you are not expecting it.  The authorised administrators of \n" +
                        "the ${tenancyDomain} account in ${encityClientName} should have pre-agreed your consent for access to HMRC data \n" +
                        "and you are under no obligation to authorise.\n\n" +

                        "Please do not reply to this email as it is auto-generated, and replies will not be seen or actioned.\n\n" +

                        "    USE THIS URL TO AUTHORISE: ${authoriseUrl}\n\n" +

                        "Kind regards,\n\n" +

                        "${encityClientName} on behalf of ${tenancyDomain}";

        StringSubstitutor sub = new StringSubstitutor(contentMap);
        String resolvedString = sub.replace(msgTemplate);

        try {
            String toAddr = evt.getContactEmail();
            this.smtpMailer.sendEmail(toAddr, subject, resolvedString);
            logger.debug( "Email sent successfully to: " + toAddr);
        } catch (MessagingException mex) {
            logger.error("Email failed due to: " + mex.getMessage());
        }
    }
}
