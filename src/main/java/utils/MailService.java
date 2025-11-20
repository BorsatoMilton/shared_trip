package utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class MailService {

    private final String host = "smtp.gmail.com";
    private final int port = 587;
    private final String username = "sharedtrip6@gmail.com";
    private final String password = "trzc pmfv auzh tmwg"; // clave de app, NO tu clave real

    private final Session session;

    public MailService() {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));

        this.session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                }
        );
    }

    public void enviarTexto(String to, String subject, String body) throws MessagingException {
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject);
        msg.setText(body);

        Transport.send(msg);
    }

    public void enviarHtml(String to, String subject, String html) throws MessagingException {
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject);
        msg.setContent(html, "text/html; charset=utf-8");

        Transport.send(msg);
    }
}
