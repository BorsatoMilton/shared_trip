package servlet;

import entities.Viaje;
import logic.ViajeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedList;

@WebServlet("/buscar")
public class BuscadorViajes extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ViajeController viajeController = new ViajeController();
    private static final Logger logger = LoggerFactory.getLogger(BuscadorViajes.class);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public BuscadorViajes() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        try {
            String origen = request.getParameter("origen");
            String destino = request.getParameter("destino");
            String fecha = request.getParameter("fecha");

            LinkedList<Viaje> viajesResultado = viajeController.getAllBySearch(origen, destino, fecha);

            request.setAttribute("viajes", viajesResultado);
            request.getRequestDispatcher("index.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error al obtener la lista de viajes: {}", e.getMessage());
            session.setAttribute("error", "Ocurrió un error al buscar los viajes. Intente más tarde.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }


}