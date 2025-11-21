package utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import io.github.cdimascio.dotenv.Dotenv;

import entidades.Usuario;

public class MailService {

    Dotenv dotenv = Dotenv.load();
    private final String host = "smtp.gmail.com";
    private final int port = 587;
    private final String username = dotenv.get("MAIL_ADRESS");
    private final String password = dotenv.get("MAIL_PASSWORD");
    private final String appUrl = dotenv.get("APP_URL");

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

    private String cargarTemplate(String nombreTemplate, Map<String, String> parametros) {
        try {

            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream(nombreTemplate + ".html");

            if (inputStream == null) {
                throw new IOException("Template no encontrado: " + nombreTemplate + ".html");
            }

            String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            for (Map.Entry<String, String> entry : parametros.entrySet()) {
                template = template.replace("{{" + entry.getKey() + "}}",
                        entry.getValue() != null ? entry.getValue() : "");
            }

            template = template.replaceAll("\\{\\{.*?\\}\\}", "");

            return template;

        } catch (IOException e) {
            System.err.println("Error cargando template " + nombreTemplate + ": " + e.getMessage());
            return null;
        }
    }

    public void notificarReservaRealizadaUsuario(String emailUsuario, String datosViaje, String datosChofer, int totalReservas, int cod_reserva) throws MessagingException {
        Map<String, String> parametros = new HashMap<>();
        parametros.put("datosViaje", datosViaje);
        parametros.put("datosChofer", datosChofer);
        parametros.put("totalReservas", String.valueOf(totalReservas));
        parametros.put("codReserva", String.valueOf(cod_reserva));

        String html = cargarTemplate("reserva-realizada-usuario-template", parametros);
        enviarHtml(emailUsuario, "Reserva confirmada - SharedTrip", html);
    }

    public void notificarReservaRealizadaChofer(String emailChofer, String datosViaje, String datosPasajero, int totalReservas) throws MessagingException {
        Map<String, String> parametros = new HashMap<>();
        parametros.put("datosViaje", datosViaje);
        parametros.put("datosPasajero", datosPasajero);
        parametros.put("totalReservas", String.valueOf(totalReservas));

        String html = cargarTemplate("reserva-realizada-chofer-template", parametros);
        enviarHtml(emailChofer, "Nueva reserva en tu viaje - SharedTrip", html);
    }

    public void notificarCancelacionReservaUsuario(String emailUsuario, String datosViaje,
                                                   String datosChofer) throws MessagingException {
        Map<String, String> parametros = new HashMap<>();
        parametros.put("datosViaje", datosViaje);
        parametros.put("datosChofer", datosChofer);

        String html = cargarTemplate("reserva-cancelada-usuario-template", parametros);
        enviarHtml(emailUsuario, "Reserva cancelada - SharedTrip", html);
    }

    public void notificarCancelacionReservaChofer(String emailChofer, String datosViaje,
                                                  String datosPasajero, int asientosLiberados,
                                                  int nuevoTotalReservas) throws MessagingException {
        Map<String, String> parametros = new HashMap<>();
        parametros.put("datosViaje", datosViaje);
        parametros.put("datosPasajero", datosPasajero);
        parametros.put("asientosLiberados", String.valueOf(asientosLiberados));
        parametros.put("nuevoTotalReservas", String.valueOf(nuevoTotalReservas));


        String html = cargarTemplate("reserva-cancelada-chofer-template", parametros);
        enviarHtml(emailChofer, "Cancelación de reserva - SharedTrip", html);
    }

    public void notificarCancelacionViajeUsuarios(String emailUsuario, String datosViaje,
                                                  String datosChofer) throws MessagingException {
        Map<String, String> parametros = new HashMap<>();
        parametros.put("datosViaje", datosViaje);
        parametros.put("datosChofer", datosChofer);

        String html = cargarTemplate("viaje-cancelado-usuario-template", parametros);
        enviarHtml(emailUsuario, "Viaje cancelado - SharedTrip", html);
    }

    public void notificarCancelacionViajeChofer(String emailChofer, String datosViaje,
                                                int totalReservas, int totalPasajeros) throws MessagingException {
        Map<String, String> parametros = new HashMap<>();
        parametros.put("datosViaje", datosViaje);
        parametros.put("totalReservas", String.valueOf(totalReservas));
        parametros.put("totalPasajeros", String.valueOf(totalPasajeros));

        String html = cargarTemplate("viaje-cancelado-chofer-template", parametros);
        enviarHtml(emailChofer, "Viaje cancelado - SharedTrip", html);
    }

    public void notificarFeedback(Usuario pasajero, String datosViaje, String token)  throws MessagingException {
        Map<String, String> parametros = new HashMap<>();
        String linkFeedback = appUrl + "/feedback?t=" + token;

        parametros.put("nombrePasajero", pasajero.getNombre());
        parametros.put("datosViaje", datosViaje);
        parametros.put("linkFeedback", linkFeedback);

        String html = cargarTemplate("feedback-pasajero-template", parametros);
        enviarHtml(pasajero.getCorreo(), "Nos interesa conocer tu opinión - SharedTrip", html);

    }


    public void enviarHtml(String to, String subject, String html) throws MessagingException {
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject);
        msg.setContent(html, "text/html; charset=utf-8");

        Transport.send(msg);
    }

    public void enviarTexto(String to, String subject, String body) throws MessagingException {
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject);
        msg.setText(body);

        Transport.send(msg);
    }
}
