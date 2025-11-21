package servlet;

import data.ReservaDAO;
import data.FeedbackDAO;
import entidades.Feedback;
import entidades.Reserva;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/feedback")
public class CRUDfeedback extends HttpServlet {

    private ReservaDAO reservaDAO = new ReservaDAO();
    private FeedbackDAO feedbackDAO = new FeedbackDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        System.out.println("Entré al doGet de CRUDfeedback");
        String token = request.getParameter("t");

        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Token inválido");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        try {
            Reserva reserva = reservaDAO.getByToken(token);
            String validationError = validarReserva(reserva);

            if (validationError != null) {
                session.setAttribute("error", validationError);
                request.getRequestDispatcher("/index.jsp").forward(request, response);
                return;
            }

            request.setAttribute("reserva", reserva);
            request.setAttribute("token", token);
            request.getRequestDispatcher("feedback.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error al buscar la reserva");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String token = request.getParameter("t");
        String comentario = request.getParameter("comentario");
        String puntuacionStr = request.getParameter("puntuacion");

        if (token == null || token.trim().isEmpty() || puntuacionStr == null || puntuacionStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Faltan datos obligatorios.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/feedback.jsp").forward(request, response);
            return;
        }

        int puntuacion = 0;
        try {
            puntuacion = Integer.parseInt(puntuacionStr);
            if (puntuacion < 1 || puntuacion > 5) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            request.setAttribute("errorMessage", "Puntuación inválida.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/feedback.jsp").forward(request, response);
            return;
        }

        try {
            Reserva reserva = reservaDAO.getByToken(token);
            String validationError = validarReserva(reserva);

            if (validationError != null) {
                session.setAttribute("error", validationError);
                request.getRequestDispatcher("/index.jsp").forward(request, response);
                return;
            }

            feedbackDAO.guardarFeedback(comentario, puntuacion, token);
            session.setAttribute("mensaje", "Feedback otorgado exitosamente!");
            response.sendRedirect(request.getContextPath() + "/index.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error al guardar el feedback");
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

        Feedback feedbackExistente = feedbackDAO.getByReserva(reserva);
        if (feedbackExistente.getFecha_hora() != null) {
            return "Ya has enviado feedback para esta reserva";
        }

        return null;
    }
}