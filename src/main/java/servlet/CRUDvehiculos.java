package servlet;

import java.io.IOException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import entidades.Usuario;
import entidades.Vehiculo;
import logic.UserController;
import logic.VehiculoController;



/**
 * Servlet implementation class CRUDvehiculos
 */
@WebServlet("/vehiculos")
public class CRUDvehiculos extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private VehiculoController vehiculoCtrl = new VehiculoController();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CRUDvehiculos() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.removeAttribute("vehiculos");
		HttpSession session = request.getSession();
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		

		LinkedList<Vehiculo> vehiculos = vehiculoCtrl.getVehiculosUsuario(usuario);
		

		request.setAttribute("vehiculos", vehiculos);
		
		request.getRequestDispatcher("misVehiculos.jsp").forward(request, response);
		;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		HttpSession session = request.getSession();
		String action = request.getParameter("action");
		
		System.out.println("ID Vehiculo: " + request.getParameter("idVehiculo"));
		


		try {
			if ("update".equals(action)) {
				actualizarVehiculo(request);
				session.setAttribute("mensaje", "Vehiculo actualizado con éxito");
			} else if ("delete".equals(action)) {
				eliminarVehiculo(request);
				session.setAttribute("mensaje", "Vehiculo eliminado con éxito");
			} else if ("add".equals(action)) {
				crearVehiculo(request);
				session.setAttribute("mensaje", "Vehiculo creado con éxito");
			}

		} catch (Exception e) {
			session.setAttribute("error", "Error: " + e.getMessage());
			if ("update".equals(action)) {
				System.out.println("Error en actualizarVehiculo: " + e.getMessage());
			}
			else if ("delete".equals(action)) {
				System.out.println("Error en eliminarVehiculo: " + e.getMessage());
			}
			else {
				System.out.println("Error en crearVehiculo: " + e.getMessage());
			}
			
		}

		response.sendRedirect(request.getContextPath() + "/vehiculos");
	}

	private void crearVehiculo(HttpServletRequest request) throws Exception {
		Vehiculo v = new Vehiculo();
		cargarDatosVehiculo(request, v);

		vehiculoCtrl.altaVehiculo(v);
	}

	private void actualizarVehiculo(HttpServletRequest request) throws Exception {
		int id = Integer.parseInt(request.getParameter("idVehiculo"));
		
		Vehiculo v = vehiculoCtrl.getOne(id);

		if (v == null) {
			throw new Exception("Vehiculo no encontrado");
		}

		cargarDatosVehiculo(request, v);
		vehiculoCtrl.actualizarVehiculo(v, id);
	}

	private void eliminarVehiculo(HttpServletRequest request) throws Exception {
		int id = Integer.parseInt(request.getParameter("idVehiculo"));
		
		Vehiculo v = vehiculoCtrl.getOne(id);
		
		if (v == null) {
			throw new Exception("Vehiculo no encontrado");
		}

		vehiculoCtrl.eliminarVehiculo(v.getId_vehiculo());
	}

	private void cargarDatosVehiculo(HttpServletRequest request, Vehiculo v) {
		HttpSession session = request.getSession();
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		
		
		v.setId_vehiculo(Integer.parseInt(request.getParameter("idVehiculo")));
		v.setPatente(request.getParameter("patente"));
		v.setModelo(request.getParameter("modelo"));
		int anio = Integer.parseInt(request.getParameter("anio"));
        v.setAnio(anio);
        v.setUsuario_duenio_id(usuario.getIdUsuario());
        
    
	}
		
	}


