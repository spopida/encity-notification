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

    @Value("${mail.smtp.host}")
    private String mailSMTPHost;

    @Value("${javax.mail.username}")
    private String mailUserName;

    @Value("${javax.mail.password}")
    private String mailPassWord;

    @Value("${mail.smtp.port}")
    private String mailSMTPPort;

    @Value("${mail.smtp.auth}")
    private String mailSMTPAuth;

    @Value("${mail.smtp.ssl.enable}")
    private String mailSMTPSSL;

    @Value("${mail.debug}")
    private String mailDebug;

    @Value("${mail.from}")
    private String fromAddr;

    @Value("${uk.co.encity.notification.confirm.url}")
    private String confirmURL;

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
            logger.debug("Tenancy created event de-serialised successfully");
        } catch (IOException e) {
            logger.error("Error de-serialising tenancy created event: " + e.getMessage());
        }

        // Create an email to the authorised contact

        String tenancyId = evt.getTenancyId();

        String msgText = "Dear " + evt.getAuthorisedContact().getFirstName() + " " + evt.getAuthorisedContact().getLastName() + "\n" +
                         "  Tariff: " + evt.getTariff() + "\n" +
                         "  Expiry: " + evt.getExpiryTime() + "\n" +
                         "  URL: " + this.confirmURL + "/" + tenancyId + "?action=confirm&uuid=" + evt.getConfirmUUID();

        // Send an email
        String to = evt.getAuthorisedContact().getEmailAddress();
        String from = this.fromAddr;

        // Create properties, get Session
        Properties props = new Properties();

        props.put("mail.smtp.host", this.mailSMTPHost);
        props.put("mail.smtp.port", this.mailSMTPPort);
        props.put("mail.smtp.auth", this.mailSMTPAuth);
        props.put("mail.debug", this.mailDebug);
        props.put("mail.smtp.ssl.enable", this.mailSMTPSSL);
        Session session = Session.getInstance(
            props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailUserName, mailPassWord);
                }
            });
        try {
            Message msg = new MimeMessage(session);

            //Set message attributes
            msg.setFrom(new InternetAddress(from));
            InternetAddress[] address = {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(this.subject);
            msg.setSentDate(new Date());

            // Set message content
            msg.setText(msgText);

            //Send the message
            Transport.send(msg);
            logger.debug("Email sent successfully; tenancy id: " + tenancyId);
        }
        catch (MessagingException mex) {
            // Prints all nested (chained) exceptions as well
            mex.printStackTrace();
        }
    }
}
