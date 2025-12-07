package services;

import data.FeedbackDAO;
import data.ReservaDAO;
import entities.Feedback;
import entities.Reserva;
import entities.Usuario;
import entities.Viaje;
import utils.Generators;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FeedbackService {
    MailService mailService = MailService.getInstance();
    private final FeedbackDAO feedbackDAO = new FeedbackDAO();
    private final Generators generators = new Generators();


    public void procesarFeedbackPendiente() throws Exception {
        ReservaDAO reservaDAO = new ReservaDAO();
        LinkedList<Reserva> reservas = reservaDAO.getReservasForFeedback();

        List<Feedback> listaFeedbacks = new ArrayList<>();

        for (Reserva reserva : reservas) {
            Usuario pasajero = reserva.getPasajero();
            Viaje viaje = reserva.getViaje();
            Usuario chofer = viaje.getConductor();

            String token = generators.generarToken();
            Feedback feedback = new Feedback(chofer, reserva, token);
            listaFeedbacks.add(feedback);

            reservaDAO.guardarToken(reserva.getIdReserva(), token);
            enviarNotificacionesFeedback(viaje, pasajero, chofer, token);
        }

        feedbackDAO.addAll(listaFeedbacks);
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
