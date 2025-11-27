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

@WebServlet("/buscar")
public class BuscadorViajes extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ViajeController viajeController = new ViajeController();

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
            e.printStackTrace();
            session.setAttribute("error", "Ocurrió un error al buscar los viajes. Intente más tarde.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }


}