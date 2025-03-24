package servlet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import entidades.Usuario;
import entidades.Viaje;
import logic.UserController;
import logic.ViajeController;

@WebServlet("/viajes")
public class CRUDviajes extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ViajeController viajeCtrl = new ViajeController();
	private UserController usuarioCtrl = new UserController();

	public CRUDviajes() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		request.removeAttribute("viajes");
		request.removeAttribute("usuarios");

		LinkedList<Viaje> viajes = viajeCtrl.getAll();
		LinkedList<Usuario> usuarios = usuarioCtrl.getAll();
		for (Viaje v : viajes) {
			Usuario usuario = usuarioCtrl.getOneById(v.getConductor().getIdUsuario());
			v.setConductor(usuario);
		}

		request.setAttribute("viajes", viajes);
		request.setAttribute("usuarios", usuarios);
		request.getRequestDispatcher("misViajes.jsp").forward(request, response);
		;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String action = request.getParameter("action");
		
		System.out.println("ID Viaje: " + request.getParameter("idViaje"));
		


		try {
			if ("update".equals(action)) {
				actualizarViaje(request);
				session.setAttribute("mensaje", "Viaje actualizado con éxito");
			} else if ("delete".equals(action)) {
				eliminarViaje(request);
				session.setAttribute("mensaje", "Viaje eliminado con éxito");
			} else if ("add".equals(action)) {
				crearViaje(request);
				session.setAttribute("mensaje", "Viaje creado con éxito");
			}

		} catch (Exception e) {
			session.setAttribute("error", "Error: " + e.getMessage());
			if ("update".equals(action)) {
				System.out.println("Error en actualizarViaje: " + e.getMessage());
			}
			else if ("delete".equals(action)) {
				System.out.println("Error en eliminarViaje: " + e.getMessage());
			}
			else {
				System.out.println("Error en crearViaje: " + e.getMessage());
			}
			
		}

		response.sendRedirect(request.getContextPath() + "/misViajes");
	}

	private void crearViaje(HttpServletRequest request) throws Exception {
		Viaje v = new Viaje();
		cargarDatosViaje(request, v);

		viajeCtrl.altaViaje(v);
	}

	private void actualizarViaje(HttpServletRequest request) throws Exception {
		int id = Integer.parseInt(request.getParameter("idViaje"));
		
		Viaje v = viajeCtrl.getOne(id);

		if (v == null) {
			throw new Exception("Viaje no encontrado");
		}

		cargarDatosViaje(request, v);
		viajeCtrl.updateViaje(v, id);
	}

	private void eliminarViaje(HttpServletRequest request) throws Exception {
		int id = Integer.parseInt(request.getParameter("idViaje"));
		Viaje v = viajeCtrl.getOne(id);

		if (v == null) {
			throw new Exception("Viaje no encontrado");
		}

		viajeCtrl.deleteViaje(v);
	}

	private void cargarDatosViaje(HttpServletRequest request, Viaje v) {
		v.setIdViaje(Integer.parseInt(request.getParameter("idViaje")));
		String fechaStr = request.getParameter("fecha"); 
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date utilDate = formato.parse(fechaStr); 
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime()); 
            v.setFecha(sqlDate); 
        } catch (ParseException e) {
            e.printStackTrace();
        }
        v.setLugares_disponibles(Integer.parseInt(request.getParameter("lugares_disponibles")));
        v.setOrigen(request.getParameter("origen"));
		v.setDestino(request.getParameter("destino"));
        v.setPrecio_unitario(Double.parseDouble(request.getParameter("precio_unitario")));
        
        v.setCancelado(false);
		v.setLugar_salida(request.getParameter("lugar_salida"));


        
		

	}

}
