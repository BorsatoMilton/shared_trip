<%@ page import="java.util.LinkedList"%>
<%@ page import="entidades.Vehiculo" %>
<%@ page import="entidades.Usuario" %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Mis Vehículos</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
	
<style>
.card-header {
	background: linear-gradient(45deg, #3f51b5, #2196f3);
	color: white;
}

.table-hover tbody tr:hover {
	background-color: #f8f9fa;
}

#anio{
	width:189px;
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
<jsp:include page="header.jsp"></jsp:include>
<div class="container-fluid p-0">
		<div class="row align-items-start" style="height: 10vh">
			<div class="col">

			</div>
		</div>
		
		<div class="main-content">	
			<div class="container-fluid p-0">
	
	
				<main class="container mt-4">
				<div class="card shadow-lg">
					<div
					class="card-header d-flex justify-content-between align-items-center">
                        <h3 class="mb-0">
                            <i class="bi bi-car-front-fill"></i> Administración de Vehículos
                        </h3>
                        <% if("usuario".equals(request.getSession().getAttribute("rol"))){ %>
                            <button type="button" id="btnNuevoVehiculo" class="btn btn-light" data-bs-toggle="modal"
                                    data-bs-target="#nuevoVehiculo">
                                <i class="bi bi-plus-circle me-2"></i>Nuevo Vehículo
                            </button>
                            <%
                        }    %>
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
			            session.removeAttribute("error");
			        	}
		   			 %>
					<div class="scrollable-table">
					<table class="table table-hover table-borderless">
						<thead class="table-light">
							<tr>
								<th scope="col">ID Vehiculo</th>
				                <th scope="col">Patente</th>
				                <th scope="col">Modelo</th>
				                <th scope="col">Anio</th>
				                <th scope="col">Id usuario dueño</th>
				                <th scope="col">Acciones</th>
						  </tr>
						</thead>
						<tbody>
								<%
				                LinkedList<Vehiculo> vehiculos= (LinkedList<Vehiculo>) request.getAttribute("vehiculos");
				                if (vehiculos != null && !vehiculos.isEmpty()) {
				                    for (Vehiculo vehiculo : vehiculos) {
				                    	 
				            	%>
				            <tr class="align-middle">
				                <td><%= vehiculo.getId_vehiculo() %></td>
				                <td><%= vehiculo.getPatente() %></td>
				                <td><%= vehiculo.getModelo() %></td>
				                <td><%= vehiculo.getAnio() %></td>
				                <td><%= vehiculo.getUsuario_duenio_id() %></td>
								<td class="text-end action-buttons">

										<button type="button"
											class="btn btn-sm btn-warning btn-editar"
											data-id="<%=vehiculo.getId_vehiculo()%>"
											data-patente="<%=vehiculo.getPatente()%>"
											data-modelo="<%=vehiculo.getModelo()%>"
											data-anio="<%=vehiculo.getAnio()%>"
											data-usuario-duenio-id="<%=vehiculo.getUsuario_duenio_id()%>">
											
											<i class="bi bi-pencil"></i>
										</button>
										
										<button type="button"
											class="btn btn-sm btn-danger btn-eliminar"
											data-id="<%=vehiculo.getId_vehiculo()%>">
											<i class="bi bi-trash"></i>
										</button>

									</td>
							</tr>
							<%
		                    }
		                } else {
		            %>
							<tr>
								<td colspan="8" class="text-center">No existen vehículos.</td>
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
</div>

<!-------------------------------------------------  Modal EDITAR VEHICULO ----------------------------------------------------------------------->

    <div class="modal fade" id="editarVehiculo" tabindex="-1" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="nuevoVehiculoLabel">
						<i class="bi bi-person-plus me-2"></i>Editar Vehiculo
					</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<form method="POST" action="vehiculos" id="formEditar">
					<input type="hidden" name="action" value="update"> <input
						type="hidden" name="idVehiculo" id="editId">
					
					<div class="modal-body">
						<div class="mb-3">

							<div class="row g-2">
								<div class="col">
								<label class="form-label">Patente</label>
									<input type="text" class="form-control" placeholder="patente"
										name="patente" id="editPatente" required>
								</div>
								<div class="col">
									<label class="form-label">Modelo</label>
									<input type="text" class="form-control" placeholder="Modelo"
										name="modelo" id="editModelo" required>
								</div>
								
							</div>
						</div>
						
						<div class="mb-3">
							<label class="form-label">Anio</label>
							<div class="row g-2">
								<div class="col">
									<input type="number" class="form-control" placeholder="anio"
										name="anio" id="editAnio" required>
								</div>
							</div>
						</div>

					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary"
							data-bs-dismiss="modal">Cancelar</button>
						<button type="submit" class="btn btn-primary">Guardar
							Vehiculo</button>
					</div>
                </form>

            </div>
		
        </div>
    </div>
	
<!-------------------------------------------------------------------- MODAL BORRAR VEHICULO ----------------------------------------------------------------->
    <div class="modal fade" id="borrarVehiculo" tabindex="-1" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">Confirmar eliminación</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
				</div>
				<div class="modal-body">
					¿Estás seguro de eliminar el vehiculo <span id="nombreVehiculo"></span>?
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">Cancelar</button>
					<form id="formEliminar" method="POST" action="vehiculos">
						<input type="hidden" name="action" value="delete"> <input
							type="hidden" name="idVehiculo" id="idVehiculoEliminar">
						<button type="submit" class="btn btn-danger">Eliminar</button>
					</form>
				</div>
			</div>
		</div>
	</div>    
    
    	
    <!--  Modal  -------------------MODAL NUEVO VEHICULO------------------------------------------>


<div class="modal fade" id="nuevoVehiculo" tabindex="-1" aria-labelledby="nuevoVehiculoLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content shadow">
      <div class="modal-header bg-primary text-white">
        <h5 class="modal-title" id="nuevoVehiculoLabel">
          <i class="bi bi-car-front-fill me-2"></i> Nuevo Vehículo
        </h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <form method="POST" action="vehiculos" id="altaVehiculo">
          <input type="hidden" name="action" value="add">
          
          <div class="row">
            <div class="col-md-6">
              <div class="mb-3">
                <label class="form-label fw-bold" for="patente">Patente</label>
                <input type="text" class="form-control" name="patente" placeholder="Ingrese la patente" id="patente" required>
              </div>
            </div>

            <div class="col-md-6">
              <div class="mb-3">
                <label class="form-label fw-bold" for="modelo">Modelo</label>
                <input type="text" class="form-control" name="modelo" placeholder="Ingrese el modelo" id="modelo" required>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="col-md-6">
              <div class="mb-3">
                <label class="form-label fw-bold" for="anio">Año</label>
                <input type="number" class="form-control" name="anio" placeholder="Ingrese el año" id="anio" required>
              </div>
            </div>
             <%--
              <% if(("admin").equals(((Usuario) session.getAttribute("usuario")).getNombreRol())){ %>
              <div class="col-md-6">
                  <div class="mb-3">
                      <label class="form-label fw-bold" for="usuario">Usuario</label>
                      <select class="form-control" name="usuario" placeholder="Ingrese el usuario" id="usuario" required>
                          <%
                              for (Usuario usuario : usuarios){
                          %> <option value="<%=usuario.getId()%>"><%=usuario.getNombre() + " " + usuario.getApellido()%></option>
                             <% }
                          %>
                      </select>
                  </div>
              </div>
              <% } %>
            --%>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
            <button type="submit" class="btn btn-primary">
              <i class="bi bi-save me-2"></i>Guardar
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>

    <div class="row align-items-end" style="height: 10vh">
        <div class="col">
            <jsp:include page="footer.jsp"/>
        </div>
    </div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
<script src="js/notificacionesTiempo.js"></script>
<script src="js/scriptVehiculos.js"></script>
</body>
</html>