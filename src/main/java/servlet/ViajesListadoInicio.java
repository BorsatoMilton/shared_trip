package servlet;

import java.io.IOException;
import java.util.LinkedList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import entidades.Viaje;
import logic.ViajeController;

/**
 * Servlet implementation class ViajesListado
 */
@WebServlet(urlPatterns = {"", "/ViajesListado"})
public class ViajesListadoInicio extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ViajeController viajeController = new ViajeController();


    @Override
    public void init() throws ServletException {}

    public ViajesListadoInicio() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        try {
            LinkedList<Viaje> viajes = viajeController.getAll(false);
            request.setAttribute("viajes", viajes);
            request.getRequestDispatcher("index.jsp").forward(request, response);

        } catch (Exception e) {
            session.setAttribute("error", "Error al obtener los viajes");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

}
