package services;

import utils.Generators;
import data.FeedbackDAO;
import data.ReservaDAO;
import entidades.Feedback;
import entidades.Reserva;
import entidades.Viaje;
import entidades.Usuario;
import java.util.LinkedList;

public class FeedbackService {
    MailService mailService = MailService.getInstance();
    private FeedbackDAO feedbackDAO = new FeedbackDAO();
    private Generators generators = new Generators();


    public void procesarFeedbackPendiente() throws Exception {
        ReservaDAO reservaDAO = new ReservaDAO();

        LinkedList<Reserva> reservas = reservaDAO.getReservasForFeedback();
        ;
        for (Reserva reserva : reservas) {
            Usuario pasajero = reserva.getPasajero();
            Viaje viaje = reserva.getViaje();
            Usuario chofer = viaje.getConductor();

            if(feedbackDAO.getByReserva(reserva) == null){

                String token = generators.generarToken();
                Feedback feedback = new Feedback(viaje.getConductor(), reserva,token);
                feedbackDAO.add(feedback);
                reservaDAO.guardarToken(reserva.getIdReserva(), token);
                enviarNotificacionesFeedback(viaje, pasajero, chofer, token);
            }
        }
    }

    private void enviarNotificacionesFeedback(Viaje viaje, Usuario pasajero, Usuario chofer, String token) {
        try {

            String datosViaje = formatDatosViaje(viaje, chofer);

            mailService.notificarFeedback(
                    pasajero,
                    datosViaje,
                    token
                    );

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
        String nombreCompleto = (chofer.getNombre() != null ? chofer.getNombre() : "No especificado")
                + (chofer.getApellido() != null ? " " + chofer.getApellido() : "");

        return String.format(
                "Origen: %s<br>Destino: %s<br>Fecha: %s<br>Conductor: %s",
                origen, destino, fecha, nombreCompleto
        );
    }


}
