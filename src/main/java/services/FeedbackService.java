package services;

import data.FeedbackDAO;
import data.ReservaDAO;
import data.ViajeDAO;
import data.UserDAO;
import entidades.Feedback;
import entidades.Reserva;
import entidades.Viaje;
import entidades.Usuario;
import jakarta.mail.MessagingException;
import utils.MailService;

import java.util.LinkedList;

public class FeedbackService {
    private MailService mailService = new MailService();
    private FeedbackDAO feedbackDAO = new FeedbackDAO();


    public void procesarFeedbackPendiente() throws Exception {
        ReservaDAO reservaDAO = new ReservaDAO();

        LinkedList<Reserva> reservas = reservaDAO.getReservasForFeedback();
        ;
        for (Reserva reserva : reservas) {
            Usuario pasajero = reserva.getPasajero();
            Viaje viaje = reserva.getViaje();
            Usuario chofer = viaje.getConductor();

            /*if(feedbackDAO.getByReserva(reserva).size() == 0){
                Feedback feedback = new Feedback(viaje.getConductor(), reserva);
                feedbackDAO.add(feedback);
                System.out.println(feedback);
                enviarNotificacionesFeedback(viaje, pasajero, chofer);
            }*/
            enviarNotificacionesFeedback(viaje, pasajero, chofer);
        }
    }

    private void enviarNotificacionesFeedback(Viaje viaje, Usuario pasajero, Usuario chofer) {
        try {

            String datosViaje = formatDatosViaje(viaje, chofer);

            mailService.notificarFeedback(
                    pasajero.getCorreo(),
                    datosViaje
            );

        } catch (MessagingException e) {
            System.err.println("Error enviando emails de feedback: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error preparando notificaciones de feedback: " + e.getMessage());
        }
    }


    private String formatDatosViaje(Viaje viaje, Usuario chofer) {
        if (viaje == null) {
            return "Información del viaje no disponible";
        }

        String origen = viaje.getOrigen() != null ? viaje.getOrigen() : "No especificado";
        String destino = viaje.getDestino() != null ? viaje.getDestino() : "No especificado";
        String fecha = viaje.getFecha() != null ? viaje.getFecha().toString() : "No especificado";
        String lugar_salida = viaje.getLugar_salida() != null ? viaje.getLugar_salida() : "No especificado";
        String nombreCompleto = (chofer.getNombre() != null ? chofer.getNombre() : "No especificado")
                + (chofer.getApellido() != null ? " " + chofer.getApellido() : "");

        return String.format(
                "Origen: %s<br>Destino: %s<br>Lugar de Salida: %s<br>Fecha: %s<br>Conductor: %s",
                origen, destino, lugar_salida, fecha, nombreCompleto
        );
    }


}
