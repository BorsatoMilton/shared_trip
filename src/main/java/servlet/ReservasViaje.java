package servlet;

import com.google.gson.Gson;
import entidades.Reserva;
import entidades.Usuario;
import logic.ReservaController;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@WebServlet("/obtenerReservas")
public class ReservasViaje extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ReservaController reservaController = new ReservaController();
    private Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) (session != null ? session.getAttribute("usuario") : null);

        if (usuario == null) {
            enviarError(response, "Sesión expirada");
            return;
        }

        String idViajeStr = request.getParameter("idViaje");

        if (idViajeStr == null || idViajeStr.trim().isEmpty()) {
            enviarError(response, "ID de viaje requerido");
            return;
        }

        try {
            int idViaje = Integer.parseInt(idViajeStr);

            LinkedList<Reserva> reservas = reservaController.getReservasPorViaje(idViaje, true);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", true);
            respuesta.put("reservas", reservas);
            respuesta.put("total", reservas.size());

            enviarRespuesta(response, respuesta);

        } catch (NumberFormatException e) {
            enviarError(response, "ID de viaje inválido");
        } catch (Exception e) {
            enviarError(response, "Error al obtener reservas: " + e.getMessage());
        }
    }

    private void enviarRespuesta(HttpServletResponse response, Map<String, Object> data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(data));
    }

    private void enviarError(HttpServletResponse response, String mensaje) throws IOException {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("mensaje", mensaje);
        enviarRespuesta(response, error);
    }
}