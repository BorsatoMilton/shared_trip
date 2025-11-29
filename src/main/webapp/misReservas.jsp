<%@page import="java.util.Date" %>
<%@ page import="entidades.Reserva" %>
<%@ page import="java.util.LinkedList" %>
<%@ page import="entidades.Usuario" %>

<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mis Reservas</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        .table-hover tbody tr:hover {
            background-color: #f8f9fa;
        }

        .action-buttons .btn {
            padding: 0.375rem 0.75rem;
        }

        .scrollable-table {
            overflow-x: auto;
        }

        .main-content {
            flex: 1 0 auto;
            width: 100%;
            padding-bottom: 60px;
        }

        body {
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        .card-header {
            background: linear-gradient(45deg, #3f51b5, #2196f3);
            color: white;
        }
    </style>
</head>

<body>
<div class="container-fluid p-0">
    <div class="row align-items-start" style="height: 10vh">
        <div class="col">
            <jsp:include page="header.jsp"></jsp:include>
        </div>
    </div>
</div>

<div class="main-content">
    <div class="container-fluid p-0">
        <main class="container mt-4">
            <div class="card shadow-lg">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h3 class="mb-0">
                        <i class="bi bi-journal-check me-1"></i>Administración de Reservas
                    </h3>
                    <div>
                        <jsp:include page="buscadorUniversal.jsp"/>
                    </div>
                </div>

                <div class="card-body">
                    <%
                        String mensaje = (String) session.getAttribute("mensaje");
                        if (mensaje != null) {
                    %>
                    <div class="alert alert-info alert-dismissible fade show" role="alert">
                        <%=mensaje%>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"
                                aria-label="Close"></button>
                    </div>
                    <%
                            session.removeAttribute("mensaje");
                        }

                        String error = (String) session.getAttribute("error");
                        if (error != null) {
                    %>
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <%=error%>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"
                                aria-label="Close"></button>
                    </div>
                    <%
                            session.removeAttribute("error");
                        }
                    %>
                    <div class="scrollable-table">
                        <table class="table table-hover table-borderless" id="tablaPrincipal">
                            <thead class="table-light">
                            <tr>
                                <th scope="col">Origen</th>
                                <th scope="col">Destino</th>
                                <th scope="col">Fecha de Viaje</th>
                                <th scope="col">Pasajeros</th>
                                <% if("usuario".equals(((Usuario) session.getAttribute("usuario")).getNombreRol())) {%>
                                <th scope="col">Total</th>
                                <% } %>

                                <th scope="col">Estado</th>

                                <% if("usuario".equals(((Usuario) session.getAttribute("usuario")).getNombreRol())) {%>
                                <th scope="col">Código Reserva</th>
                                <% } %>

                                <% if("admin".equals(((Usuario) session.getAttribute("usuario")).getNombreRol())) {%>
                                <th scope="col">Reservante</th>
                                <th scope="col">Conductor</th>
                                <% } %>

                                <th scope="col" class="text-end">Acciones</th>
                            </tr>
                            </thead>
                            <tbody>
                            <%
                                LinkedList<Reserva> reservas = (LinkedList<Reserva>) request.getSession().getAttribute("reservas");
                                if (reservas != null && !reservas.isEmpty()) {
                                    for (Reserva reserva : reservas) {
                                        Date hoy = new Date();
                                        boolean deshabilitar = reserva.isReserva_cancelada()
                                                || "CONFIRMADA".equals(reserva.getEstado())
                                                || "CANCELADA".equals(reserva.getEstado())
                                                || reserva.getViaje().getFecha().before(hoy);
                            %>
                            <tr class="align-middle">
                                <td><%= reserva.getViaje().getOrigen() %></td>
                                <td><%= reserva.getViaje().getDestino() %></td>
                                <td><%= reserva.getViaje().getFecha() %></td>
                                <td><%= reserva.getCantidad_pasajeros_reservada()%></td>
                                <% if("usuario".equals(((Usuario) session.getAttribute("usuario")).getNombreRol())) {%>
                                    <td>
                                        $<%= reserva.getViaje().getPrecio_unitario() * reserva.getCantidad_pasajeros_reservada()%>
                                    </td>
                                <% } %>

                                <td><%= reserva.getEstado() %></td>

                                <% if("usuario".equals(((Usuario) session.getAttribute("usuario")).getNombreRol())) {%>
                                    <td><%= reserva.getCodigo_reserva()%></td>
                                <% } %>

                                <% if("admin".equals(((Usuario) session.getAttribute("usuario")).getNombreRol())) {%>
                                <td><%= reserva.getPasajero().getNombre() + " " + reserva.getPasajero().getApellido()%><br>
                                    <%= reserva.getPasajero().getCorreo()%>
                                </td>
                                <td><%= reserva.getViaje().getConductor().getNombre() + " " + reserva.getViaje().getConductor().getApellido()%><br>
                                    <%= reserva.getViaje().getConductor().getCorreo()%>
                                </td>
                                <% } %>
                                <td class="text-end action-buttons">
                                    <% if("EN PROCESO".equals(reserva.getEstado())) { %>
                                    <button type="button" class="btn btn-sm btn-danger btn-cancelar"
                                            data-id="<%=reserva.getIdReserva()%>">
                                        <i class="bi bi-x-circle-fill"></i>
                                    </button>
                                    <% }if("CONFIRMADA".equals(reserva.getEstado()) || "CANCELADA".equals(reserva.getEstado())) { %>
                                    <button type="button" class="btn btn-sm btn-danger btn-eliminar"
                                            data-id="<%=reserva.getIdReserva()%>">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                    <% }%>
                                </td>
                            </tr>
                            <%
                                }
                            } else {
                            %>
                            <tr>
                                <td colspan="8" class="text-center">No existen reservas.</td>
                            </tr>
                            <%
                                }
                            %>
                            <tr id="noResultados" style="display:none;">
                                <td colspan="8" style="padding:10px; color:dodgerblue; font-size:large;">
                                    No existen resultados.
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>

<footer>
    <div class="row align-items-end" style="height: 10vh">
        <div class="col">
            <jsp:include page="footer.jsp"></jsp:include>
        </div>
    </div>
</footer>


<!-- MODAL ELIMINAR RESERVA -->
<div class="modal fade" id="eliminarReserva" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Confirmar eliminación</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                ¿Estás seguro de eliminar esta reserva?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <form id="formCancelar" action="reservas" method="post" class="d-inline">
                    <input type="hidden" name="action" value="eliminar">
                    <input type="hidden" name="reservaId" id="idReservaEliminar">
                    <button type="submit" class="btn btn-danger">Eliminar Reserva</button>
                </form>
            </div>
        </div>
    </div>
</div>


<!-- MODAL CANCELAR RESERVA -->
<div class="modal fade" id="cancelarReserva" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Confirmar cancelación</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                ¿Estás seguro de cancelar esta reserva?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <form id="formCancelar" action="reservas" method="post" class="d-inline">
                    <input type="hidden" name="action" value="cancelar">
                    <input type="hidden" name="reservaId" id="idReservaCancelar">
                    <button type="submit" class="btn btn-danger">Cancelar Reserva</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
<script src="<%= request.getContextPath() %>/js/notificacionesTiempo.js"></script>
<script src="<%= request.getContextPath() %>/js/scriptReservas.js"></script>
<script src="<%= request.getContextPath() %>/js/buscadorUniversal.js"></script>
</body>
</html>