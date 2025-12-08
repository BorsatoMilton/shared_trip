package servlet;

import entities.Feedback;
import entities.Reserva;
import logic.FeedbackController;
import logic.ReservaController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/feedback")
public class CRUDfeedback extends HttpServlet {

    private final ReservaController reservaController = new ReservaController();
    private final FeedbackController feedbackController = new FeedbackController();
    private static final Logger logger = LoggerFactory.getLogger(CRUDfeedback.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("t");
        HttpSession session = request.getSession();

        if (token == null || token.trim().isEmpty()) {
            session.setAttribute("error", "Token inválido");
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        try {
            Reserva reserva = reservaController.getByToken(token);
            String validationError = validarReserva(reserva);

            if (validationError != null) {
                session.setAttribute("error", validationError);
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }

            request.setAttribute("reserva", reserva);
            request.setAttribute("token", token);
            request.getRequestDispatcher("feedback.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error al obtener los feedback del usuario: {}", e.getMessage());
            session.setAttribute("error", "Error al buscar la reserva");
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter("t");
        String puntuacionStr = request.getParameter("puntuacion");
        HttpSession session = request.getSession();

        if (token == null || token.trim().isEmpty() || puntuacionStr == null || puntuacionStr.trim().isEmpty()) {
            session.setAttribute("error", "Faltan datos obligatorios.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/feedback.jsp").forward(request, response);
            return;
        }

        int puntuacion = 0;
        try {
            puntuacion = Integer.parseInt(puntuacionStr);
            if (puntuacion < 1 || puntuacion > 5) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            session.setAttribute("error", "Puntuación inválida.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/feedback.jsp").forward(request, response);
            return;
        }

        try {
            Reserva reserva = reservaController.getByToken(token);
            String validationError = validarReserva(reserva);

            if (validationError != null) {
                session.setAttribute("error", validationError);
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }

            feedbackController.guardarFeedback(puntuacion, reserva.getIdReserva());
            session.setAttribute("mensaje", "Feedback otorgado exitosamente!");
            response.sendRedirect(request.getContextPath() + "/");

        } catch (Exception e) {
            logger.error("Error al guardar el feedback: {}", e.getMessage());
            session.setAttribute("error", "Error al guardar el feedback");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/feedback.jsp").forward(request, response);
        }
    }


    private String validarReserva(Reserva reserva) {

        if (reserva == null) {
            return "No existe la reserva para ese token";
        }

        String estado = reserva.getEstado();

        if (estado == null || estado.trim().isEmpty() || !estado.equalsIgnoreCase("CONFIRMADA")) {
            return "Esta reserva no es válida para enviar feedback";
        }

        Feedback feedbackExistente = feedbackController.getByReserva(reserva);
        if (feedbackExistente == null) {
            return "No existe el feedback con ese token ni reserva";
        }
        if (feedbackExistente.getFecha_hora() != null) {
            return "Ya has enviado feedback para esta reserva";
        }

        return null;
    }
}