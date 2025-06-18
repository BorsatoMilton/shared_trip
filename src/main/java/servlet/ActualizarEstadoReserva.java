package servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import data.ReservaDAO;
import entidades.Reserva;
import entidades.Usuario;
import logic.ReservaController;

/**
 * Servlet implementation class ActualizarEstadoReserva
 */
@WebServlet("/ActualizarEstadoReserva")
public class ActualizarEstadoReserva extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ReservaController reservaCtrl = new ReservaController();
		
		int idReserva = Integer.parseInt(request.getParameter("idReserva"));
		Reserva reserva = reservaCtrl.getOne(idReserva);
	    int codigoCorrecto = reserva.getViaje().getCodigoValidacion();
	    
	    int codigoIngresado = Integer.parseInt(request.getParameter("codigo_validacion_usuario"));
	    
	    int intentos = reserva.getIntentos_codigo();

	    if (intentos <= 0) {
	    	reservaCtrl.actualizarEstado(idReserva,"INVALIDADA" );
	        return;
	    }

	    if (codigoIngresado == codigoCorrecto) {
	    	reservaCtrl.actualizarEstado(idReserva,"PAGADA" );
	        
	    	
	        
	    } else {
	        intentos--;
	        reserva.setIntentos_codigo(intentos);
	        

	        if (intentos <= 0) {
	        	reservaCtrl.actualizarEstado(idReserva,"INVALIDADA" );
	        }

	    }
	    reservaCtrl.updateEntity(reserva, idReserva);
	    response.sendRedirect("misReservas");
	}
	

}
