<%@ page import="java.util.LinkedList" %>
<%@ page import="entidades.Viaje" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.ZoneId" %>
<%@ page import="java.sql.Date" %>
<%@ page import="entidades.Vehiculo" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="entidades.Usuario" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="ISO-8859-1">
    <title>Mis Viajes</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        .opcion {
            background-color: white;
            padding: 6px 8px;
            cursor: pointer;
            line-height: 1.2;
            border-bottom: 1px solid #eee;
        }

        .opcion:hover {
            background-color: #f0f7ff;
        }

        .opcion.active {
            background-color: #3B71CA;
            color: white;
        }

        .resultadoCiudades .no-results {
            padding: 8px;
            color: #666;
        }

        .resultadoCiudades {
            position: absolute;
            width: 100%;
            border: 1px solid #ccc;
            background-color: white;
            max-height: 200px;
            overflow-y: auto;
            z-index: 9999;
            display: none;
            border-radius: 5px;
            box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
            margin-top: 5px;
        }

        .buscar-limpiar {
            width: 48%;
        }

        .dropdown-container {
            position: relative;
            width: 100%;
        }

        .card-header {
            background: linear-gradient(45deg, #3f51b5, #2196f3);
            color: white;
        }

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
                        <i class="bi bi-people-fill me-2"></i>Administración de Viajes
                    </h3>
                    <% if ("usuario".equals(((Usuario)session.getAttribute("usuario")).getNombreRol())) { %>
                        <button type="button" class="btn btn-light" data-bs-toggle="modal"
                            data-bs-target="#nuevoViaje">
                        <i class="bi bi-plus-circle me-2"></i>Nuevo Viaje
                    </button>
                    <% } %>
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
                        <table class="table table-hover table-borderless">
                            <thead class="table-light">
                            <tr>
                                <th scope="col">Fecha</th>
                                <th scope="col">Origen</th>
                                <th scope="col">Destino</th>
                                <th scope="col">Lugar de Salida</th>
                                <% if("usuario".equals(((Usuario) session.getAttribute("usuario")).getNombreRol())) { %>
                                    <th scope="col">Lugares</th>
                                    <th scope="col">Precio Unitario</th>
                                <% } else {%>
                                    <th scope="col">Conductor</th>
                                <% } %>
                                <th scope="col">Cancelado</th>
                                <th scope="col" class="text-end">Acciones</th>
                            </tr>
                            </thead>
                            <tbody>
                            <%
                                LinkedList<Vehiculo> vehiculos = (LinkedList<Vehiculo>) request.getAttribute("vehiculos");
                                LinkedList<Viaje> viajes = (LinkedList<Viaje>) request.getAttribute("viajes");

                                if (viajes != null && !viajes.isEmpty()) {
                                    for (Viaje viaje : viajes) {
                            %>
                            <tr class="align-middle">
                                <td><%= viaje.getFecha() %>
                                </td>
                                <td><%= viaje.getOrigen() %>
                                </td>
                                <td><%= viaje.getDestino() %>
                                </td>
                                <td><%= viaje.getLugar_salida() %>
                                </td>
                                <% if("usuario".equals(((Usuario) session.getAttribute("usuario")).getNombreRol())) { %>
                                <td><%= viaje.getLugares_disponibles() %>
                                </td>
                                <td>$<%= viaje.getPrecio_unitario() %>
                                </td>
                                <% } else { %>
                                <td>
                                    <%= viaje.getConductor().getNombre() + " " + viaje.getConductor().getApellido()%><br>
                                    <%= viaje.getConductor().getCorreo()%>
                                </td>
                                <% } %>
                                <td><%= viaje.isCancelado() ? "Sí" : "No" %>
                                </td>
                                <td class="text-end action-buttons">
                                    <% if (!viaje.isCancelado()) { %>
                                    <button type="button"
                                            class="btn btn-sm btn-warning btn-editar"
                                            data-id="<%=viaje.getIdViaje()%>"
                                            data-fecha="<%=viaje.getFecha()%>"
                                            data-lugares_disponibles="<%=viaje.getLugares_disponibles()%>"
                                            data-origen="<%=viaje.getOrigen()%>"
                                            data-destino="<%=viaje.getDestino()%>"
                                            data-precio_unitario="<%=viaje.getPrecio_unitario()%>"
                                            data-cancelado="<%=viaje.isCancelado()%>"
                                            data-id_conductor="<%=viaje.getConductor()%>"
                                            data-lugar_salida="<%=viaje.getLugar_salida()%>">
                                        <i class="bi bi-pencil"></i>
                                    </button>
                                    <% } %>
                                    <% if (viaje.isCancelado()) { %>
                                    <button type="button"
                                            class="btn btn-sm btn-danger btn-eliminar"
                                            data-id="<%=viaje.getIdViaje()%>">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                    <% } %>

                                    <%
                                        Date fechaViaje = viaje.getFecha();
                                        LocalDateTime fechaViajeLocalDateTime = fechaViaje.toLocalDate().atStartOfDay();
                                    %>

                                    <% if (!viaje.isCancelado()) { %>
                                    <button type="button" class="btn btn-sm btn-danger btn-cancelar"
                                            data-id="<%=viaje.getIdViaje()%>"
                                            <% if (viaje.isCancelado() || fechaViajeLocalDateTime.isBefore(LocalDateTime.now())) { %>
                                            disabled
                                            <% } %>>
                                        <i class="bi bi-x-circle-fill"></i>
                                    </button>
                                    <% } %>

                                    <%
                                        LocalDate fechaViajeLocal = null;
                                        if (viaje.getFecha() != null) {
                                            fechaViajeLocal = viaje.getFecha().toLocalDate();
                                        }
                                    %>
                                    <% if (fechaViajeLocal != null && fechaViajeLocal.equals(LocalDate.now()) && !viaje.isCancelado()) { %>
                                    <button type="button"
                                            class="btn btn-sm btn-primary btn-codigo"
                                            data-id="<%= viaje.getIdViaje() %>">
                                        <i class="bi bi-key"></i> Código
                                    </button>
                                    <% } %>
                                </td>
                            </tr>
                            <%
                                }
                            } else {
                            %>
                            <tr>
                                <td colspan="8" class="text-center">No existen viajes.</td>
                            </tr>
                            <%
                                }
                            %>
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

<!-- MODAL INTRODUCIR CODIGO -->
<div class="modal fade" id="introducirCodigo" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Validar Reserva</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form id="formCodigoValidacion" method="POST" action="reservas">
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="codigo" class="form-label">Código de validación</label>
                        <input type="number" class="form-control" name="codigo" id="codigo"
                               placeholder="Ingrese el código de validación" required>
                    </div>
                    <input type="hidden" name="action" value="validate">
                    <input type="hidden" name="idViaje" id="idViajeAverificar">
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Verificar</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- MODAL EDITAR VIAJE -->
<div class="modal fade" id="editarViaje" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-person-plus me-2"></i>Editar Viaje
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form method="POST" action="viajes" id="formEditar">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="idViaje" id="editId">

                <div class="modal-body">
                    <div class="row g-2 mb-3">
                        <div class="col-12 col-md-4">
                            <label for="editFecha" class="form-label">Fecha de Viaje:</label>
                            <input type="date" class="form-control" id="editFecha" name="fecha">
                        </div>
                        <div class="col">
                            <label class="form-label">Lugar de Salida</label>
                            <input type="text" class="form-control"
                                   placeholder="Lugar Salida"
                                   name="lugar_salida"
                                   id="editLugarSalida"
                                   required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="editOrigen" class="form-label">Origen:</label>
                        <div class="dropdown-container">
                            <input type="text" class="form-control" id="editOrigen"
                                   name="origen" placeholder="Ciudad de origen"
                                   required autocomplete="off">
                            <div id="resultadoCiudadesOrigenEdit" class="resultadoCiudades"></div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="editDestino" class="form-label">Destino:</label>
                        <div class="dropdown-container">
                            <input type="text" class="form-control" id="editDestino"
                                   name="destino" placeholder="Ciudad de destino"
                                   required autocomplete="off">
                            <div id="resultadoCiudadesDestinoEdit" class="resultadoCiudades"></div>
                        </div>
                    </div>

                    <div class="row g-2 mb-3">
                        <div class="col">
                            <label class="form-label">Lugares Disponibles</label>
                            <input type="number" class="form-control"
                                   placeholder="Lugares disponibles"
                                   name="lugares_disponibles"
                                   id="editLugaresDisponibles" required>
                        </div>
                        <div class="col">
                            <label class="form-label">Precio Unitario</label>
                            <input type="number" step="any" class="form-control"
                                   placeholder="Precio unitario"
                                   name="precio_unitario"
                                   id="editPrecioUnitario" required>
                        </div>
                    </div>
                    <% if("usuario".equals(((Usuario)session.getAttribute("usuario")).getNombreRol())) { %>
                        <div class="mb-3">
                            <label class="form-label" for="editIdVehiculo">Vehículo</label>
                            <select name="idVehiculo" id="editIdVehiculo" class="form-select w-100">
                                <% if (vehiculos != null && !vehiculos.isEmpty()) {
                                    for (Vehiculo v : vehiculos) { %>
                                <option value="<%=v.getId_vehiculo()%>"><%=v.getPatente()%>
                                </option>
                                <% }
                                } %>
                            </select>
                        </div>
                    <% } %>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Guardar Viaje</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- MODAL BORRAR VIAJE -->
<div class="modal fade" id="borrarViaje" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Confirmar eliminación</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                ¿Estás seguro de eliminar este viaje?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <form id="formEliminar" method="POST" action="viajes" class="d-inline">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="idViaje" id="idViajeEliminar">
                    <button type="submit" class="btn btn-danger">Eliminar</button>
                </form>
            </div>
        </div>
    </div>
</div>


<!-- MODAL CANCELAR VIAJE -->
<div class="modal fade" id="cancelarViaje" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Confirmar cancelación</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                ¿Estás seguro de cancelar este viaje?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <form id="formCancelar" action="viajes" method="post" class="d-inline">
                    <input type="hidden" name="idViaje" id="idViajeCancelar">
                    <input type="hidden" name="action" value="cancelarViaje">
                    <button type="submit" class="btn btn-danger">Cancelar Viaje</button>
                </form>
            </div>
        </div>
    </div>
</div>


<!-- MODAL NUEVO VIAJE -->
<div class="modal fade" id="nuevoViaje" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Nuevo Viaje</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body">
                <form method="POST" action="viajes" id="altaViaje">
                    <input type="hidden" name="action" value="add">

                    <div class="row">

                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="fecha" class="form-label">Fecha de Viaje</label>
                                <input type="date" class="form-control" id="fecha" name="fecha" required>
                            </div>

                            <div class="mb-3">
                                <label for="lugares_disponibles" class="form-label">Lugares Disponibles</label>
                                <input type="number" class="form-control" name="lugares_disponibles"
                                       id="lugares_disponibles" placeholder="Ingrese los lugares disponibles" required>
                            </div>

                            <div class="mb-3">
                                <label for="newOrigen" class="form-label">Origen</label>
                                <div class="dropdown-container">
                                    <input type="text" class="form-control" id="newOrigen" name="origen"
                                           placeholder="Ciudad de origen" required autocomplete="off">
                                    <div id="resultadoCiudadesOrigenNew" class="resultadoCiudades"></div>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="precio_unitario" class="form-label">Precio Unitario</label>
                                <input type="number" step="any" class="form-control" name="precio_unitario"
                                       id="precio_unitario" placeholder="Ingrese el precio unitario" required>
                            </div>

                            <div class="mb-3">
                                <label for="lugar_salida" class="form-label">Lugar de Salida</label>
                                <input type="text" class="form-control" name="lugar_salida" id="lugar_salida"
                                       placeholder="Ingrese el lugar de salida" required>
                            </div>

                            <div class="mb-3">
                                <label for="newDestino" class="form-label">Destino</label>
                                <div class="dropdown-container">
                                    <input type="text" class="form-control" id="newDestino" name="destino"
                                           placeholder="Ciudad de destino" required autocomplete="off">
                                    <div id="resultadoCiudadesDestinoNew" class="resultadoCiudades"></div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-12">
                            <div class="mb-3">
                                <label class="form-label" for="idVehiculoNuevo">Vehículo</label>
                                <select name="idVehiculo" id="idVehiculoNuevo" class="form-select w-100" required>
                                    <% if (vehiculos != null && !vehiculos.isEmpty()) { %>
                                    <% for (Vehiculo v : vehiculos) { %>
                                    <option value="<%= v.getId_vehiculo() %>"><%= v.getPatente() %></option>
                                    <% } %>
                                    <% } else { %>
                                    <option value="">-- No hay vehículos cargados. Agregue uno. --</option>
                                    <% } %>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                        <button type="submit" class="btn btn-primary">Guardar</button>
                    </div>
                </form>
            </div>

        </div>
    </div>
</div>


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
<script src="<%= request.getContextPath() %>/js/notificacionesTiempo.js"></script>
<script src="<%= request.getContextPath() %>/js/buscadorMunicipios.js"></script>
<script src="<%= request.getContextPath() %>/js/scriptViajes.js"></script>
</body>
</html>