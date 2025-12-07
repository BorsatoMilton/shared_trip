package servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import entities.Usuario;
import entities.Viaje;
import entities.Reserva;
import logic.UserController;
import logic.ViajeController;
import logic.ReservaController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/dashboard")
public class DashBoard extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ViajeController viajeCtrl = new ViajeController();
    private final UserController usuarioCtrl = new UserController();
    private final ReservaController reservaCtrl = new ReservaController();
    private static final Logger logger = LoggerFactory.getLogger(DashBoard.class);

    public DashBoard() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!"admin".equalsIgnoreCase(usuario.getRol().getNombreRol())) {
            session.setAttribute("error", "Acceso denegado. Solo administradores pueden acceder al dashboard.");
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        try {
            LinkedList<Viaje> todosViajes = viajeCtrl.getAll(true);
            LinkedList<Usuario> todosUsuarios = usuarioCtrl.getAll();
            LinkedList<Reserva> todasReservas = reservaCtrl.getAllReservas();

            Map<String, Object> kpis = calcularKPIsAdmin(todasReservas, todosViajes, todosUsuarios);
            Map<String, Object> metricasFinancieras = calcularMetricasFinancieras();
            Map<String, Object> estadisticasUsuarios = calcularEstadisticasUsuarios();
            
            LinkedList<Reserva> reservasRecientes = obtenerReservasRecientes();
            LinkedList<Viaje> viajesProximos = obtenerViajesProximos();
            LinkedList<Usuario> usuariosRecientes = obtenerUsuariosRecientes();

            request.setAttribute("kpis", kpis);
            request.setAttribute("metricasFinancieras", metricasFinancieras);
            request.setAttribute("estadisticasUsuarios", estadisticasUsuarios);
            request.setAttribute("reservasRecientes", reservasRecientes);
            request.setAttribute("viajesProximos", viajesProximos);
            request.setAttribute("usuariosRecientes", usuariosRecientes);
            
            request.getRequestDispatcher("WEB-INF/dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            logger.error("Error al cargar el dashboard: {}", e.getMessage());
            session.setAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
    
    
    private Map<String, Object> calcularKPIsAdmin(LinkedList<Reserva> reservas, 
                                                 LinkedList<Viaje> viajes, 
                                                 LinkedList<Usuario> usuarios) {
        Map<String, Object> kpis = new HashMap<>();
        
        kpis.put("totalUsuarios", usuarios.size());
        kpis.put("totalViajes", viajes.size());
        kpis.put("totalReservas", reservas.size());
        
        int viajesActivos = 0;
        for (int i = 0; i < viajes.size(); i++) {
            Viaje viaje = viajes.get(i);
            if (!viaje.isCancelado()) {
                viajesActivos++;
            }
        }
        kpis.put("viajesActivos", viajesActivos);
        
        int viajesCancelados = 0;
        for (int i = 0; i < viajes.size(); i++) {
            Viaje viaje = viajes.get(i);
            if (viaje.isCancelado()) {
                viajesCancelados++;
            }
        }
        kpis.put("viajesCancelados", viajesCancelados);
        
        return kpis;
    }
    
    private Map<String, Object> calcularMetricasFinancieras() {
        Map<String, Object> metricas = new HashMap<>();
        
        double ingresosTotales = reservaCtrl.getIngresosTotales();
        double ingresosMesActual = reservaCtrl.getIngresosMesActual();
        double promedioReserva = reservaCtrl.getPromedioPorReserva();
        
        metricas.put("ingresosTotales", ingresosTotales);
        metricas.put("ingresosMesActual", ingresosMesActual);
        metricas.put("promedioReserva", promedioReserva);
        
        logger.info("Métricas financieras calculadas - Ingresos totales: {}, Mes actual: {}, Promedio: {}", 
                    ingresosTotales, ingresosMesActual, promedioReserva);
        
        return metricas;
    }
    
    private Map<String, Object> calcularEstadisticasUsuarios() {
        Map<String, Object> stats = new HashMap<>();
        
        Map<String, Integer> estadisticas = usuarioCtrl.getEstadisticasUsuarios();
        
        stats.put("totalConductores", estadisticas.getOrDefault("totalConductores", 0));
        stats.put("totalPasajeros", estadisticas.getOrDefault("totalPasajeros", 0));
        stats.put("totalAdmins", estadisticas.getOrDefault("totalAdmins", 0));
        stats.put("totalUsuarios", estadisticas.getOrDefault("totalUsuarios", 0));
        
        logger.info("Estadísticas de usuarios - Conductores: {}, Pasajeros: {}, Admins: {}, Total: {}", 
                    stats.get("totalConductores"), stats.get("totalPasajeros"), 
                    stats.get("totalAdmins"), stats.get("totalUsuarios"));
        
        return stats;
    }
    
    private LinkedList<Reserva> obtenerReservasRecientes() {
        return reservaCtrl.obtenerReservasRecientes(5);
    }
    
    private LinkedList<Viaje> obtenerViajesProximos() {
        return viajeCtrl.obtenerViajesProximos(5);
    }
    
    private LinkedList<Usuario> obtenerUsuariosRecientes() {
        return usuarioCtrl.getUsuariosRecientes(5);
    }
}