package servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import entidades.Usuario;
import entidades.Viaje;
import logic.ViajeController;

/**
 * Servlet implementation class AltaViaje
 */
@WebServlet({"/AltaViaje", "/altaViaje"})
public class AltaViaje extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AltaViaje() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Usuario usuario = (Usuario)request.getSession().getAttribute("usuario");		
		Viaje v = new Viaje();
		ViajeController viajeCtrl = new ViajeController();
		
		v.setDestino(request.getParameter("destino"));
		v.setOrigen(request.getParameter("origen"));
		v.setLugar_salida(request.getParameter("lugar_salida"));
        v.setLugares_disponibles(Integer.parseInt(request.getParameter("lugares_disponibles")));
        v.setCancelado(false);

        String fechaStr = request.getParameter("fecha"); 
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date utilDate = formato.parse(fechaStr); 
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime()); 
            v.setFecha(sqlDate); 
        } catch (ParseException e) {
            e.printStackTrace();
        }
        v.setPrecio_unitario(Double.parseDouble(request.getParameter("precio_unitario")));
        
        v.setConductor(usuario);
	       
		viajeCtrl.altaViaje(v);
		response.sendRedirect("misViajes");
	}

}
