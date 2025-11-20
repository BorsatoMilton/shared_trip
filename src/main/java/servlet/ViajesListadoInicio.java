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
    private ViajeController viajeController;


    @Override
    public void init() throws ServletException {
        viajeController = new ViajeController();
    }

    public ViajesListadoInicio() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();

        LinkedList<Viaje> viajes = viajeController.getAll();
        request.setAttribute("viajes", viajes);

        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}
