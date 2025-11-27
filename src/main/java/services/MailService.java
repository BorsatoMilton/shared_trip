package services;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;

import io.github.cdimascio.dotenv.Dotenv;
import entidades.Usuario;

public final class MailService {

    private static final MailService INSTANCE = new MailService();

    private final Dotenv dotenv = Dotenv.load();
    private final String host = "smtp.gmail.com";
    private final int port = 587;
    private final String username = dotenv.get("MAIL_ADRESS");
    private final String password = dotenv.get("MAIL_PASSWORD");
    private final String appUrl = dotenv.get("APP_URL");

    private final Session session;
    private final ExecutorService executor;

    private MailService() {
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

        this.executor = Executors.newFixedThreadPool(8);
    }

    public static MailService getInstance() {
        return INSTANCE;
    }


    public void notificarReservaRealizadaUsuario(String emailUsuario, String datosViaje, String datosChofer, int totalReservas, int cod_reserva) {
        Map<String, String> parametros = new HashMap<>();
        parametros.put("datosViaje", datosViaje);
        parametros.put("datosChofer", datosChofer);
        parametros.put("totalReservas", String.valueOf(totalReservas));
        parametros.put("codReserva", String.valueOf(cod_reserva));

        String html = cargarTemplate("reserva-realizada-usuario-template", parametros);

        runAsync(() -> {
            try {
                enviarHtmlSync(emailUsuario, "Reserva confirmada - SharedTrip", html);
            } catch (Exception e) {
                System.err.println("[MailService] Error enviando mail usuario: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void notificarReservaRealizadaChofer(String emailChofer, String datosViaje, String datosPasajero, int totalReservas) {
        Map<String, String> parametros = new HashMap<>();
        parametros.put("datosViaje", datosViaje);
        parametros.put("datosPasajero", datosPasajero);
        parametros.put("totalReservas", String.valueOf(totalReservas));

        String html = cargarTemplate("reserva-realizada-chofer-template", parametros);

        runAsync(() -> {
            try {
                enviarHtmlSync(emailChofer, "Nueva reserva en tu viaje - SharedTrip", html);
            } catch (Exception e) {
                System.err.println("[MailService] Error enviando mail chofer: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void notificarCancelacionReservaUsuario(String emailUsuario, String datosViaje, String datosChofer) {
        Map<String, String> parametros = new HashMap<>();
        parametros.put("datosViaje", datosViaje);
        parametros.put("datosChofer", datosChofer);

        String html = cargarTemplate("reserva-cancelada-usuario-template", parametros);

        runAsync(() -> {
            try {
                enviarHtmlSync(emailUsuario, "Reserva cancelada - SharedTrip", html);
            } catch (Exception e) {
                System.err.println("[MailService] Error enviando mail cancelación usuario: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void notificarCancelacionReservaChofer(String emailChofer, String datosViaje, String datosPasajero, int asientosLiberados, int nuevoTotalReservas) {
        Map<String, String> parametros = new HashMap<>();
        parametros.put("datosViaje", datosViaje);
        parametros.put("datosPasajero", datosPasajero);
        parametros.put("asientosLiberados", String.valueOf(asientosLiberados));
        parametros.put("nuevoTotalReservas", String.valueOf(nuevoTotalReservas));

        String html = cargarTemplate("reserva-cancelada-chofer-template", parametros);

        runAsync(() -> {
            try {
                enviarHtmlSync(emailChofer, "Cancelación de reserva - SharedTrip", html);
            } catch (Exception e) {
                System.err.println("[MailService] Error enviando mail cancelación chofer: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void notificarCancelacionViajeUsuarios(String emailUsuario, String datosViaje, String datosChofer) {
        Map<String, String> parametros = new HashMap<>();
        parametros.put("datosViaje", datosViaje);
        parametros.put("datosChofer", datosChofer);

        String html = cargarTemplate("viaje-cancelado-usuario-template", parametros);

        runAsync(() -> {
            try {
                enviarHtmlSync(emailUsuario, "Viaje cancelado - SharedTrip", html);
            } catch (Exception e) {
                System.err.println("[MailService] Error enviando mail viaje cancelado usuario: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void notificarCancelacionViajeChofer(String emailChofer, String datosViaje, int totalReservas, int totalPasajeros) throws MessagingException {
        Map<String, String> parametros = new HashMap<>();
        parametros.put("datosViaje", datosViaje);
        parametros.put("totalReservas", String.valueOf(totalReservas));
        parametros.put("totalPasajeros", String.valueOf(totalPasajeros));

        String html = cargarTemplate("viaje-cancelado-chofer-template", parametros);

        runAsync(() -> {
            try {
                enviarHtmlSync(emailChofer, "Viaje cancelado - SharedTrip", html);
            } catch (Exception e) {
                System.err.println("[MailService] Error enviando mail viaje cancelado chofer: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void notificarFeedback(Usuario pasajero, String datosViaje, String token) {
        Map<String, String> parametros = new HashMap<>();
        String linkFeedback = appUrl + "/feedback?t=" + token;

        parametros.put("nombrePasajero", pasajero.getNombre());
        parametros.put("datosViaje", datosViaje);
        parametros.put("linkFeedback", linkFeedback);

        String html = cargarTemplate("feedback-pasajero-template", parametros);

        runAsync(() -> {
            try {
                enviarHtmlSync(pasajero.getCorreo(), "Nos interesa conocer tu opinión - SharedTrip", html);
            } catch (Exception e) {
                System.err.println("[MailService] Error enviando mail feedback: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void recuperarClave(Usuario pasajero, String token) {
        Map<String, String> parametros = new HashMap<>();
        String linkRecuperar = appUrl + "/auth?t=" + token;

        parametros.put("nombreUsuario", pasajero.getNombre());
        parametros.put("linkRecuperar", linkRecuperar);

        String html = cargarTemplate("recuperar-clave-template", parametros);

        runAsync(() -> {
            try {
                enviarHtmlSync(pasajero.getCorreo(), "Recuperación de clave - SharedTrip", html);
            } catch (Exception e) {
                System.err.println("[MailService] Error enviando mail recuperar clave: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void runAsync(Runnable task) {
        try {
            CompletableFuture.runAsync(task, executor);
        } catch (RejectedExecutionException ree) {
            System.err.println("[MailService] Tarea de envío rechazada: " + ree.getMessage());
        }
    }

    private String cargarTemplate(String nombreTemplate, Map<String, String> parametros) {
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("templates/" + nombreTemplate + ".html");

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
            System.err.println("[MailService] Error cargando template " + nombreTemplate + ": " + e.getMessage());
            return "";
        }
    }


    private void enviarHtmlSync(String to, String subject, String html) throws MessagingException {
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject);
        msg.setContent(html, "text/html; charset=utf-8");
        Transport.send(msg);
    }


    public void shutdownAndAwaitTermination() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    System.err.println("[MailService] El executor no terminó.");
                }
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
