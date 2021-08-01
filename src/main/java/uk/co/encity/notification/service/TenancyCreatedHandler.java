package uk.co.encity.notification.service;

import javax.mail.Session;
import javax.mail.PasswordAuthentication;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.MessagingException;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.util.Logger;
import reactor.util.Loggers;
import uk.co.encity.notification.events.TenancyCreatedEvent;
import uk.co.encity.notification.events.TenancyCreatedEventDeserializer;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

@Component
public class TenancyCreatedHandler {
    private final Logger logger = Loggers.getLogger(getClass());
    private final String subject = "New encity Tenancy created - please authorise";

    @Value("${uk.co.encity.notification.confirm.url}")
    private String confirmURL;

    private final SMTPMailer smtpMailer;

    public TenancyCreatedHandler(@Autowired SMTPMailer mailer) { this.smtpMailer = mailer;
    }
    public void receiveMessage(String message) {
        logger.debug("Received <" + message + ">");

        // De-serialise the JSON into a POJO
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(TenancyCreatedEvent.class, new TenancyCreatedEventDeserializer());
        mapper.registerModule(module);
        mapper.registerModule(new JavaTimeModule()); // Needed for handling java.time.Instant objects

        TenancyCreatedEvent evt = null;
        try {
            evt = mapper.readValue(message, TenancyCreatedEvent.class);
            logger.debug(this.getClass().getName() + " de-serialisation successful");
        } catch (IOException e) {
            logger.error(this.getClass().getName() + " de-serialization error: " + e.getMessage());
        }

        // Create an email to the authorised contact

        String tenancyId = evt.getTenancyId();

        String msgText = "Dear " + evt.getAuthorisedContact().getFirstName() + " " + evt.getAuthorisedContact().getLastName() + "\n" +
                         "  Tariff: " + evt.getTariff() + "\n" +
                         "  Expiry: " + evt.getExpiryTime() + "\n" +
                         "  URL: " + this.confirmURL + "tenancy/" + tenancyId + "/confirm?uuid=" + evt.getConfirmUUID();

        try {
            String subject = "New Encity tenancy created - please authorise";
            String toAddr = evt.getAuthorisedContact().getEmailAddress();
            this.smtpMailer.sendEmail(toAddr, subject, msgText);
            logger.debug( "Email sent successfully to: " + toAddr);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
