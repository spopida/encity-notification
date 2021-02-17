package uk.co.encity.notification.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Component
@Getter @NoArgsConstructor
public class SMTPMailer {
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

    public void sendEmail(String to, String subject, String text) throws MessagingException {

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

        Message msg = new MimeMessage(session);

        //Set message attributes
        msg.setFrom(new InternetAddress(this.fromAddr));
        InternetAddress[] address = {new InternetAddress(to)};
        msg.setRecipients(Message.RecipientType.TO, address);
        msg.setSubject(subject);
        msg.setSentDate(new Date());

        // Set message content
        msg.setText(text);

        //Send the message
        Transport.send(msg);
        return;
    }
}
