<%@ page import="java.util.LinkedList"%>
<%@ page import="entidades.Viaje"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.time.LocalDateTime"%>
<%@ page import="java.time.ZoneId"%>
<%@ page import="java.sql.Date"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">

<title>Mis Viajes</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
	crossorigin="anonymous">

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
	
<style>

.opcion{
	background-color: white;
	height: 30px;
	padding: 0 0 8px 8px;
	cursor:pointer; 
}

.opcion:hover {
	background-color: #3B71CA;
}

.resultadoCiudades {
    position: relative;  /* Ahora empuja los elementos hacia abajo */
    width: 100%;
    border: 1px solid #ccc;
    background-color: white;
    max-height: 200px;
    overflow-y: auto;
    z-index: 10;
    display: none;
    border-radius: 5px;
    box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
    margin-top: 5px;
}

/* Asegurar que los botones estén debajo del dropdown */
.buscar-limpiar-container {
    margin-top: 10px;  /* Ajusta la separación */
    display: flex;
    justify-content: space-between;
    width: 100%;
}

/* Botones con tamaño completo */
.buscar-limpiar {
    width: 48%;
}

.dropdown-container {
    position: relative;
    width: 100%;
    display: flex;
    flex-direction: column;
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

	<div class="container-fluid p-0"></div>
		<div class="row align-items-start" style="height: 10vh">
			<div class="col">
				<jsp:include page="header.jsp"></jsp:include>
			</div>
		</div>
		
		<div class="main-content">	
			<div class="container-fluid p-0">
	
	
				<main class="container mt-4">
				<div class="card shadow-lg">
					<div
					class="card-header d-flex justify-content-between align-items-center">
					<h3 class="mb-0">
						<i class="bi bi-people-fill me-2"></i>Administración de Viajes
					</h3>
					<button type="button" class="btn btn-light" data-bs-toggle="modal"
						data-bs-target="#nuevoViaje">
						<i class="bi bi-plus-circle me-2"></i>Nuevo Viaje
					</button>
				</div>
					
					<div class="card-body">
					<% 
			        	String mensaje = (String) session.getAttribute("mensaje");
			        	if (mensaje != null) {
		    		%>
					<div class="alert alert-info alert-dismissible fade show"
						role="alert">
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
					<div class="alert alert-danger alert-dismissible fade show"
						role="alert">
						<%=error%>
						<button type="button" class="btn-close" data-bs-dismiss="alert"
							aria-label="Close"></button>
					</div>
					<%
			            session.removeAttribute("mensaje");
			        	}
		   			 %>
					<div class="scrollable-table">
					<table class="table table-hover table-borderless">
						<thead class="table-light">
							<tr>
								<th scope="col">ID Viaje</th>
								<th scope="col">Origen</th>
								<th scope="col">Destino</th>
								<th scope="col">Fecha</th>
								<th scope="col">Lugares Disponibles</th>
								<th scope="col">Precio Unitario</th>
								<th scope="col">Lugar de Salida</th>
								<th scope="col">Cancelado</th>
								<th scope="col">Codigo Validación</th>
								<th scope="col" class="text-end">Acciones</th>
							</tr>
						</thead>
						<tbody>
							<%
		                LinkedList<Viaje> viajes = (LinkedList<Viaje>) request.getAttribute("viajes");
		                if (viajes != null && !viajes.isEmpty()) {
		                    for (Viaje viaje : viajes) {
		                    	
		            %>
							<tr class="align-middle">
								<td><%= viaje.getIdViaje() %></td>
								<td><%= viaje.getOrigen() %></td>
								<td><%= viaje.getDestino() %></td>
								<td><%= viaje.getFecha() %></td>
								<td><%= viaje.getLugares_disponibles() %></td>
								<td>$<%= viaje.getPrecio_unitario() %></td>
								<td><%= viaje.getLugar_salida() %></td>
								<td><%= viaje.isCancelado() ? "Sí" : "No" %></td>
								<td><%= viaje.getCodigoValidacion() %></td>
								<td class="text-end action-buttons">

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
										
										<button type="button"
											class="btn btn-sm btn-danger btn-eliminar"
											data-id="<%=viaje.getIdViaje()%>" <% if (!viaje.isCancelado() ){ %>
											disabled <% } %>>
											<i class="bi bi-trash"></i>
										</button>
                                        <form action="viajes" method="post">
                                            <input type="hidden" name="viajeId"
                                                   value="<%= viaje.getIdViaje() %>">
                                            <%
                                                Date fechaViaje = viaje.getFecha();
                                                LocalDateTime fechaViajeLocalDateTime = fechaViaje.toLocalDate().atStartOfDay();
                                            %>
                                            <input type="hidden" name="action" value="cancelarViaje">

                                            <button type="submit" class="btn btn-danger"
                                                    <% if (viaje.isCancelado() || fechaViajeLocalDateTime.isBefore(LocalDateTime.now())) { %>
                                                    disabled <% } %>>Cancelar</button>
                                        </form>
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
<!-------------------------------------------------  Modal EDITAR VIAJE ----------------------------------------------------------------------------------------->

<div class="modal fade" id="editarViaje">
		<div class="modal-dialog modal-dialog-centered modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="nuevoViajeLabel">
						<i class="bi bi-person-plus me-2"></i>Editar Viaje
					</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<form method="POST" action="viajes" id="formEditar">
					<input type="hidden" name="action" value="update">
                    <input type="hidden" name="idViaje" id="editId">
					
					<div class="modal-body">
						<div class="mb-3">

							<div class="row g-2">
								<div class="col">
								<label class="form-label">Fecha Viaje</label>
									<input type="date" class="form-control" placeholder="fecha"
										name="fecha" id="editFecha" required>
								</div>
								<div class="col">
									<label class="form-label">Lugar de Salida</label>
									<input type="text" class="form-control" placeholder="LugarSalida"
										name="lugar_salida" id="editLugarSalida" required>
								</div>
								
							</div>
						</div>
						
						<div class="mb-3">
							<label class="form-label">Origen</label>
							<div class="row g-2">
								<div class="col">
									<input type="text" class="form-control" placeholder="origen"
										name="origen" id="editOrigen" required>
								</div>
								<div class="col">
									<label class="form-label">Destino</label>
									<input type="text" class="form-control" placeholder="Destino"
										name="destino" id="editDestino" required>
								</div>
							</div>
						</div>
						
						<div class="mb-3">
							<label class="form-label">Lugares Disponibles</label>
							<div class="row g-2">
								<div class="col">
									<input type="number" class="form-control" placeholder="lugares_disponibles"
										name="lugares_disponibles" id="editLugaresDisponibles" required>
								</div>
								<div class="col">
									<label class="form-label">Precio Unitario</label>
									<input type="number" step=any class="form-control" placeholder="precio_unitario"
										name="precio_unitario" id="editPrecioUnitario" required>
								</div>
							</div>
						</div>

					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary"
							data-bs-dismiss="modal">Cancelar</button>
						<button type="submit" class="btn btn-primary">Guardar
							Viaje</button>
					</div>
					</form>
					</div>
			</div>
		</div>
	

<!-------------------------------------------------------------------- MODAL BORRAR VIAJE ----------------------------------------------------------------->
	<div class="modal fade" id="borrarViaje">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">Confirmar eliminación</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
				</div>
				<div class="modal-body">
					¿Estás seguro de eliminar el viaje <span id="nombreViaje"></span>?
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">Cancelar</button>
					<form id="formEliminar" method="POST" action="viajes">
						<input type="hidden" name="action" value="delete"> <input
							type="hidden" name="idViaje" id="idViajeEliminar">
						<button type="submit" class="btn btn-danger">Eliminar</button>
					</form>
				</div>
			</div>
		</div>
	</div>
<!------------------------------------------------MODAL #nuevoVehiculo --------------------------------------------------------------------------------->
<div class="modal fade" id="nuevoViaje" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
            <div class="modal-content">
              <div class="modal-header">
                <h1 class="modal-title fs-5" id="nuevoViaje">Nuevo Viaje</h1>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
              </div>
            <div class="modal-body">
                <form method="POST" action="viajes" id="altaViaje">
                    <input type="hidden" name="action" value="add">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="fecha">Fecha</label>
                                <input type="date" class="form-control" name="fecha" id="fecha" required>
                            </div>

                            <div class="mb-3">
                                <label for="lugares_disponibles">Lugares Disponibles</label>
                                <input type="text" class="form-control" name="lugares_disponibles" id="lugares_disponibles" placeholder="Ingrese los lugares disponibles " required>
                            </div>

                            <div class="mb-3">
                                <label for="origen" class="form-label">Origen:</label>
                                <div class="dropdown-container">
                                    <input type="text" class="form-control" id="origen" name="origen" placeholder="Ciudad de origen" required>
                                    <div id="resultadoCiudadesOrigen" class="resultadoCiudades"></div>
                                </div>
                            </div>

                        </div>

                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="precio_unitario">Precio Unitario</label>
                                <input type="number" step=any class="form-control" name="precio_unitario" id="precio_unitario" placeholder="Ingrese el precio unitario" required>
                            </div>

                            <div class="mb-3">
                                <label for="lugar_salida">Lugar de Salida</label>
                                <input type="text" class="form-control" name="lugar_salida" id="lugar_salida" placeholder="Ingrese el lugar de salida" required>
                            </div>

                            <div class="mb-3">
                                <label for="destino" class="form-label" >Destino:</label>
                                <div class="dropdown-container">
                                    <input type="text" class="form-control" id="destino" name="destino" placeholder="Ciudad de destino" required>
                                    <div id="resultadoCiudadesDestino" class="resultadoCiudades"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                        <button type="submit" class="btn btn-primary" onclick = "envioFormulario()">Guardar</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>		


    <script src = "scripts/buscadorMunicipios.js"></script>
    <script src="js/scriptViajes.js"></script>
    <script
            src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
            crossorigin="anonymous"></script>
</body>
</html>