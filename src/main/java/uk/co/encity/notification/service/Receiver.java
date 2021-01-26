package uk.co.encity.notification.service;

import javax.mail.Session;
import javax.mail.PasswordAuthentication;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.MessagingException;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;

import static java.lang.Integer.parseInt;

@Component
public class Receiver {
    private final Logger logger = Loggers.getLogger(getClass());

    private String mailSMTPHost;
    private String mailUserName;
    private String mailPassWord;
    private String mailSMTPPort;
    private String mailSMTPAuth;
    private String mailSMTPSSL;
    private String mailDebug;

    public Receiver(
        @Value("${mail.smtp.host}") String host,
        @Value("${mail.smtp.port}") String port,
        @Value("${mail.smtp.auth}") String auth,
        @Value("${mail.smtp.ssl.enable}") String ssl,
        @Value("${javax.mail.username}") String userName,
        @Value("${javax.mail.password}") String passWord,
        @Value("${mail.debug}") String mailDebug)
    {
        this.mailSMTPHost = host;
        this.mailSMTPPort = port;
        this.mailSMTPAuth = auth;
        this.mailSMTPSSL = ssl;
        this.mailUserName = userName;
        this.mailPassWord = passWord;
        this.mailDebug = mailDebug;
    }

    public void receiveMessage(String message) {
        logger.debug("Received <" + message + ">");

        // De-serialise the JSON into a POJO

        // Create and send an email to the authorised contact

        String to = "adrian@greenshootsaccounting.co.uk";
        String from = "ade@adrian-hall.name";

        // Create properties, get Session
        Properties props = new Properties();

        props.put("mail.smtp.host", mailSMTPHost);
        props.put("mail.smtp.port", mailSMTPPort);
        props.put("mail.smtp.auth", mailSMTPAuth);
        // To see what is going on behind the scene
        props.put("mail.debug", mailDebug);
        props.put("mail.smtp.ssl.enable", mailSMTPSSL);
        Session session = Session.getInstance(
            props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailUserName, mailPassWord);
                }
            });
        try {
            // Instantiate a message
            Message msg = new MimeMessage(session);

            //Set message attributes
            msg.setFrom(new InternetAddress(from));
            InternetAddress[] address = {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject("Test E-Mail through Java");
            msg.setSentDate(new Date());

            // Set message content
            msg.setText("This is a test of sending a " +
                    "plain text e-mail through Java.\n" +
                    "Here is line 2.");

            //Send the message
            Transport.send(msg);
        }
        catch (MessagingException mex) {
            // Prints all nested (chained) exceptions as well
            mex.printStackTrace();
        }
    }
}
