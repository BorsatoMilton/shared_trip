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

        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String tipo = usuario.getNombreRol();

        LinkedList<Viaje> viajes = new LinkedList<>();
        LinkedList<Usuario> usuarios = null;

        if ("admin".equalsIgnoreCase(tipo)) {
            viajes = viajeCtrl.getAll();
            usuarios = usuarioCtrl.getAll();
            System.out.println("Viajes: " + viajes);

        } else if ("usuario".equalsIgnoreCase(tipo)) {
            viajes = viajeCtrl.getViajesUsuario(usuario);
        }

        for (Viaje v : viajes) {
            if (v.getConductor() != null && v.getConductor().getIdUsuario() > 0) {
                Usuario u = usuarioCtrl.getOneById(v.getConductor().getIdUsuario());
                v.setConductor(u);
            }
        }

        request.setAttribute("viajes", viajes);
        request.setAttribute("usuarios", usuarios);

        request.getRequestDispatcher("misViajes.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String action = request.getParameter("action");

		try {
			if ("update".equals(action)) {
				actualizarViaje(request, session);
				session.setAttribute("mensaje", "Viaje actualizado con éxito");
			} else if ("delete".equals(action)) {
				eliminarViaje(request);
				session.setAttribute("mensaje", "Viaje eliminado con éxito");
			} else if ("add".equals(action)) {
				crearViaje(request, session);
				session.setAttribute("mensaje", "Viaje creado con éxito");
			} else if ("cancelarViaje".equals(action)) {
                boolean cancelar = cancelarViaje(request);
                if (cancelar) {
                    session.setAttribute("mensaje", "Viaje cancelado con éxito");
                } else {
                    session.setAttribute("mensaje", "Ocurrió un error al cancelar el viaje");
                }
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

		response.sendRedirect(request.getContextPath() + "/viajes");
	}

	private void crearViaje(HttpServletRequest request, HttpSession session) throws Exception {
		Viaje v = new Viaje();
		cargarDatosViaje(request, v, session);

		viajeCtrl.altaViaje(v);
	}

	private void actualizarViaje(HttpServletRequest request, HttpSession session) throws Exception {
		int id = Integer.parseInt(request.getParameter("idViaje"));
		
		Viaje v = viajeCtrl.getOne(id);

		if (v == null) {
			throw new Exception("Viaje no encontrado");
		}

		cargarDatosViaje(request, v, session);
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

    private boolean cancelarViaje(HttpServletRequest request) throws Exception {
        int idViaje = Integer.parseInt(request.getParameter("viajeId"));
        boolean cancelada = viajeCtrl.cancelar(idViaje);
        return cancelada;

    }


	private void cargarDatosViaje(HttpServletRequest request, Viaje v, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
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
        v.setConductor(usuario);
        v.setCodigoValidacion((int)(Math.random() * 900) + 100);
	}

}
