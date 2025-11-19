package servlet;

import entidades.Reserva;
import entidades.Rol;
import entidades.Usuario;
import entidades.Viaje;
import logic.ReservaController;
import logic.RolController;
import logic.UserController;
import logic.ViajeController;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

@WebServlet("/reservas")
public class CRUDreservas extends HttpServlet {
    private static final long serialVersionUID = 1L;
    ViajeController viajeController = new ViajeController();
    ReservaController reservaController = new ReservaController();


    public CRUDreservas() {
        super();
        // TODO Auto-generated constructor stub
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        LinkedList<Reserva> reservas = reservaController.getReservasUsuario(usuario);

        request.getSession().setAttribute("misreservas", reservas);
        request.getRequestDispatcher("misReservas.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        if (session.getAttribute("usuario") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try{
            if("reserve".equals(action)){
                reservar(request);
                session.setAttribute("mensaje", "Reserva realizada con éxito");
            }else if("cancelar".equals(action)){
                cancelarReserva(request);
                session.setAttribute("mensaje", "Reserva cancelada con éxito");
            }else if("updateStatus".equals(action)){
                actualizarEstadoReserva(request);
                session.setAttribute("mensaje", "Reserva actualizada");
            }
        }
        catch (Exception e){
            session.setAttribute("error", "Error: " + e.getMessage());
            System.out.println("Error en operación: " + e.getMessage());
        }

        if("reserve".equals(action)){
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }else if ("cancelar".equals(action)){
            response.sendRedirect(request.getContextPath() + "/reservas");
            return;
        }

    }

    private void reservar(HttpServletRequest request) throws Exception {

        int viajeId = Integer.parseInt(request.getParameter("viajeId"));
        int cantPasajeros = Integer.parseInt(request.getParameter("cantPasajeros"));

        Usuario user = (Usuario) request.getSession().getAttribute("usuario");
        int idUsuario = user.getIdUsuario();

        Viaje viaje = viajeController.getOne(viajeId);

        if (viaje == null) {
            throw new Exception("El viaje no existe.");
        }

        if (cantPasajeros <= 0) {
            throw new Exception("La cantidad de pasajeros debe ser mayor a cero.");
        }

        if (viaje.getLugares_disponibles() < cantPasajeros) {
            throw new Exception("No hay cupos suficientes para realizar la reserva.");
        }

        reservaController.nuevaReserva(viaje, cantPasajeros, idUsuario);
        viajeController.actualizarCantidad(viajeId, cantPasajeros);


    }

    private void cancelarReserva(HttpServletRequest request) throws Exception {

        int idReserva = Integer.parseInt(request.getParameter("reservaId"));
        int idViaje = Integer.parseInt(request.getParameter("viajeId"));

        int cantidadPasajeros = reservaController.obtenerCantidad(idReserva);
        boolean cancelada = reservaController.cancelar(idReserva);
        System.out.println("cancelada: " + cancelada);
        if (cancelada) {
            viajeController.actualizarCantidad(idViaje, cantidadPasajeros * (-1));
        }else{
            throw new Exception("Error al cancelar Reserva");
        }

    }

    private void actualizarEstadoReserva(HttpServletRequest request){
        /*ReservaController reservaCtrl = new ReservaController();

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
        response.sendRedirect("misReservas");*/
        System.out.println("En construcción ...");
    }

}