package logic;

import data.FeedbackDAO;
import entities.Feedback;
import entities.Reserva;

public class FeedbackController {
    private final FeedbackDAO feedbackDAO;

    public FeedbackController() {
        this.feedbackDAO = new FeedbackDAO();
    }

    public Feedback getByReserva(Reserva reserva) {
        if (reserva == null) return null;
        return feedbackDAO.getByReserva(reserva);
    }

    public void guardarFeedback(int puntuacion, String token) throws Exception {
        feedbackDAO.guardarFeedback(puntuacion, token);
    }
}
