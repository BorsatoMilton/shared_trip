package servlet;

import java.io.IOException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import entidades.Usuario;
import entidades.Vehiculo;
import logic.VehiculoController;


/**
 * Servlet implementation class ListadoVehiculosUsuario
 */
@WebServlet("/misVehiculos")
public class ListadoVehiculosUsuario extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ListadoVehiculosUsuario() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		VehiculoController vehiculoCtrl = new VehiculoController();
		Usuario usuario = (Usuario)request.getSession().getAttribute("usuario");
		LinkedList<Vehiculo> vehiculos = vehiculoCtrl.getVehiculosUsuario(usuario);
		request.getSession().setAttribute("misvehiculos", vehiculos);
		request.getRequestDispatcher("misVehiculos.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
