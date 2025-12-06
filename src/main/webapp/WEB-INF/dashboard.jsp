<%@ page import="java.util.LinkedList" %>
<%@ page import="java.util.Map" %>
<%@ page import="entities.Viaje" %>
<%@ page import="entities.Reserva" %>
<%@ page import="entities.Usuario" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">

    <title>Dashboard Admin - Sistema de Viajes</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/styles/dashboard.css">
</head>
<body class="bg-light">
    <div class="container-fluid p-0">
        <div class="row align-items-start">
            <div class="col">
                <jsp:include page="header.jsp"></jsp:include>
            </div>
        </div>
    </div>

    <div class="container-fluid mt-4">
        <div class="row">
            <div class="col-12">
                <div class="dashboard-section p-4 mb-4">
                    <div class="row align-items-center">
                        <div class="col-md-8">
                            <h2 class="mb-1 text-dark">
                                <i class="bi bi-speedometer2 me-2"></i>Dashboard Administrativo
                            </h2>
                            <p class="text-muted mb-0">Resumen completo del sistema de viajes</p>
                        </div>
                        <div class="col-md-4 text-end">
                            <span class="badge bg-danger fs-6">
                                <i class="bi bi-shield-check me-1"></i>Administrador
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="row mb-3 justify-content-center">
            <%
                Map<String, Object> kpis = (Map<String, Object>) request.getAttribute("kpis");
                if (kpis != null) {
            %>
            <div class="col-xl-2 col-md-4 col-sm-6 mb-3">
                <div class="card stat-card primary h-100">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <div class="kpi-number text-primary"><%= kpis.get("totalUsuarios") %></div>
                                <div class="kpi-label">Total Usuarios</div>
                            </div>
                            <i class="bi bi-people-fill stat-icon text-primary"></i>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="col-xl-2 col-md-4 col-sm-6 mb-3">
                <div class="card stat-card success h-100">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <div class="kpi-number text-success"><%= kpis.get("totalViajes") %></div>
                                <div class="kpi-label">Total Viajes</div>
                            </div>
                            <i class="bi bi-geo-alt-fill stat-icon text-success"></i>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="col-xl-2 col-md-4 col-sm-6 mb-3">
                <div class="card stat-card info h-100">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <div class="kpi-number text-info"><%= kpis.get("totalReservas") %></div>
                                <div class="kpi-label">Total Reservas</div>
                            </div>
                            <i class="bi bi-journal-check stat-icon text-info"></i>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="col-xl-2 col-md-4 col-sm-6 mb-3">
                <div class="card stat-card warning h-100">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <div class="kpi-number text-warning"><%= kpis.get("viajesActivos") %></div>
                                <div class="kpi-label">Viajes Activos</div>
                            </div>
                            <i class="bi bi-car-front stat-icon text-warning"></i>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="col-xl-2 col-md-4 col-sm-6 mb-3">
                <div class="card stat-card dark h-100">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <div class="kpi-number text-dark"><%= kpis.get("viajesCancelados") %></div>
                                <div class="kpi-label">Viajes Cancelados</div>
                            </div>
                            <i class="bi bi-slash-circle stat-icon text-dark"></i>
                        </div>
                    </div>
                </div>
            </div>
            <% } %>
        </div>


        <div class="row mb-4">
            <div class="col-lg-6 mb-4">
                <div class="dashboard-section p-4 h-100">
                    <h5 class="mb-3 text-dark">
                        <i class="bi bi-currency-dollar me-2"></i>Métricas Financieras
                    </h5>
                    <%
                        Map<String, Object> metricasFinancieras = (Map<String, Object>) request.getAttribute("metricasFinancieras");
                        if (metricasFinancieras != null) {
                    %>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <div class="border rounded p-3 bg-light">
                                <div class="text-center">
                                    <div class="h4 text-success fw-bold">
                                        $<%= String.format("%,.2f", metricasFinancieras.get("ingresosTotales")) %>
                                    </div>
                                    <small class="text-muted">Ingresos Totales</small>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="border rounded p-3 bg-light">
                                <div class="text-center">
                                    <div class="h4 text-primary fw-bold">
                                        $<%= String.format("%,.2f", metricasFinancieras.get("ingresosMesActual")) %>
                                    </div>
                                    <small class="text-muted">Ingresos Mes Actual</small>
                                </div>
                            </div>
                        </div>
                        <div class="col-12">
                            <div class="border rounded p-3 bg-light">
                                <div class="text-center">
                                    <div class="h5 text-info fw-bold">
                                        $<%= String.format("%,.2f", metricasFinancieras.get("promedioReserva")) %>
                                    </div>
                                    <small class="text-muted">Ticket Promedio por Reserva</small>
                                </div>
                            </div>
                        </div>
                    </div>
                    <% } %>
                </div>
            </div>


            <div class="col-lg-6 mb-4">
                <div class="dashboard-section p-4 h-100">
                    <h5 class="mb-3 text-dark">
                        <i class="bi bi-pie-chart me-2"></i>Estadísticas de Usuarios
                    </h5>
                    <%
                        Map<String, Object> estadisticasUsuarios = (Map<String, Object>) request.getAttribute("estadisticasUsuarios");
                        if (estadisticasUsuarios != null) {
                    %>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <div class="text-center p-3 border rounded">
                                <div class="h4 fw-bold text-primary"><%= estadisticasUsuarios.get("totalConductores") %></div>
                                <small class="text-muted">Conductores</small>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="text-center p-3 border rounded">
                                <div class="h4 fw-bold text-success"><%= estadisticasUsuarios.get("totalPasajeros") %></div>
                                <small class="text-muted">Pasajeros</small>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="text-center p-3 border rounded">
                                <div class="h4 fw-bold text-info"><%= estadisticasUsuarios.get("totalAdmins") %></div>
                                <small class="text-muted">Administradores</small>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="text-center p-3 border rounded">
                                <div class="h4 fw-bold text-warning"><%= estadisticasUsuarios.get("totalUsuarios") %></div>
                                <small class="text-muted">Total Usuarios</small>
                            </div>
                        </div>
                    </div>
                    <% } %>
                </div>
            </div>
        </div>


        <div class="row">
            <div class="col-lg-6 mb-4">
                <div class="dashboard-section p-4 h-100">
                    <h5 class="mb-3 text-dark">
                        <i class="bi bi-clock-history me-2"></i>Reservas Recientes
                    </h5>
                    <%
                        LinkedList<Reserva> reservasRecientes = (LinkedList<Reserva>) request.getAttribute("reservasRecientes");
                        if (reservasRecientes != null && !reservasRecientes.isEmpty()) {
                    %>
                    <div class="table-responsive">
                        <table class="table table-sm table-hover">
                            <thead class="table-light">
                                <tr>
                                    <th>ID</th>
                                    <th>Viaje</th>
                                    <th>Pasajero</th>
                                    <th>Estado</th>
                                    <th>Total</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Reserva reserva : reservasRecientes) { %>
                                <tr>
                                    <td><small>#<%= reserva.getIdReserva() %></small></td>
                                    <td>
                                        <small>
                                            <%= reserva.getViaje().getOrigen() %> → <%= reserva.getViaje().getDestino() %>
                                        </small>
                                    </td>
                                    <td><small><%= reserva.getPasajero().getNombre() %></small></td>
                                    <td>
                                        <%
                                            String badgeClass = "CONFIRMADA".equals(reserva.getEstado()) ? "bg-success" : 
                                                               "CANCELADA".equals(reserva.getEstado()) ? "bg-danger" : "bg-warning";
                                        %>
                                        <span class="badge badge-kpi <%= badgeClass %>"><%= reserva.getEstado() %></span>
                                    </td>
                                    <td>
                                        <small>
                                            $<%= String.format("%.2f", reserva.getViaje().getPrecio_unitario() * reserva.getCantidad_pasajeros_reservada()) %>
                                        </small>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                    <% } else { %>
                    <div class="text-center py-4">
                        <i class="bi bi-inbox display-6 text-muted"></i>
                        <p class="text-muted mt-3">No hay reservas recientes</p>
                    </div>
                    <% } %>
                </div>
            </div>

            <div class="col-lg-6 mb-4">
                <div class="row h-100">
                    <div class="col-12 mb-4">
                        <div class="dashboard-section p-4 h-100">
                            <h6 class="mb-3 text-dark">
                                <i class="bi bi-calendar-event me-2"></i>Próximos Viajes
                            </h6>
                            <%
                                LinkedList<Viaje> viajesProximos = (LinkedList<Viaje>) request.getAttribute("viajesProximos");
                                if (viajesProximos != null && !viajesProximos.isEmpty()) {
                            %>
                            <div class="list-group list-group-flush">
                                <% for (Viaje viaje : viajesProximos) { %>
                                <div class="list-group-item px-0 border-0">
                                    <div class="d-flex w-100 justify-content-between align-items-center">
                                        <div>
                                            <h6 class="mb-1 small"><%= viaje.getOrigen() %> → <%= viaje.getDestino() %></h6>
                                            <p class="mb-1 small text-muted">
                                                <%= viaje.getFecha() %> · 
                                                <span class="badge bg-info badge-kpi"><%= viaje.getLugares_disponibles() %> lugares</span>
                                            </p>
                                        </div>
                                        <small class="text-success fw-bold">$<%= viaje.getPrecio_unitario() %> c/u</small>
                                    </div>
                                </div>
                                <% } %>
                            </div>
                            <% } else { %>
                            <div class="text-center py-3">
                                <i class="bi bi-calendar-x text-muted"></i>
                                <p class="text-muted mt-2 small">No hay viajes próximos</p>
                            </div>
                            <% } %>
                        </div>
                    </div>

                    <div class="col-12">
                        <div class="dashboard-section p-4 h-100">
                            <h6 class="mb-3 text-dark">
                                <i class="bi bi-person-plus me-2"></i>Usuarios Recientes
                            </h6>
                            <%
                                LinkedList<Usuario> usuariosRecientes = (LinkedList<Usuario>) request.getAttribute("usuariosRecientes");
                                if (usuariosRecientes != null && !usuariosRecientes.isEmpty()) {
                            %>
                            <div class="list-group list-group-flush">
                                <% for (Usuario user : usuariosRecientes) { %>
                                <div class="list-group-item px-0 border-0">
                                    <div class="d-flex w-100 justify-content-between align-items-center">
                                        <div>
                                            <h6 class="mb-1 small"><%= user.getNombre() %> <%= user.getApellido() %></h6>
                                            <p class="mb-1 small text-muted"><%= user.getCorreo() %></p>
                                        </div>
                                        <span class="badge bg-secondary badge-kpi"><%= user.getNombreRol() %></span>
                                    </div>
                                </div>
                                <% } %>
                            </div>
                            <% } else { %>
                            <div class="text-center py-3">
                                <i class="bi bi-people text-muted"></i>
                                <p class="text-muted mt-2 small">No hay usuarios recientes</p>
                            </div>
                            <% } %>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="footer.jsp"></jsp:include>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="<%= request.getContextPath() %>/js/notificacionesTiempo.js"></script>
</body>
</html>