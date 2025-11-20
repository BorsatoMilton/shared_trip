<%@ page import="java.util.LinkedList"%>
<%@ page import="entidades.Usuario"%>
<%@ page import="entidades.Rol"%>
<%@ page import="logic.RolController"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Gestión de Usuarios</title>
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
<body class="bg-light">
		<jsp:include page="header.jsp"></jsp:include>
		
	<div class="main-content">	
	<div class="container-fluid p-0">


		<main class="container mt-4">
			<div class="card shadow-lg">
				<div
					class="card-header d-flex justify-content-between align-items-center">
					<h3 class="mb-0">
						<i class="bi bi-people-fill me-2"></i>Administración de Usuarios
					</h3>
					<button type="button" class="btn btn-light" data-bs-toggle="modal"
						data-bs-target="#nuevoUsuario">
						<i class="bi bi-plus-circle me-2"></i>Nuevo Usuario
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
					session.removeAttribute("error");
					}
					%>

					<div class="scrollable-table">
						<table class="table table-hover table-borderless">
							<thead class="table-light">
								<tr>
									<th scope="col">Usuario</th>
									<th scope="col">Nombre</th>
									<th scope="col">Correo</th>
									<th scope="col">Teléfono</th>
									<th scope="col">Rol</th>
									<th scope="col" class="text-end">Acciones</th>
								</tr>
							</thead>
							<tbody>
								<%
								LinkedList<Usuario> usuarios = (LinkedList<Usuario>) request.getAttribute("usuarios");
								if (usuarios != null && !usuarios.isEmpty()) {
									for (Usuario usuario : usuarios) {
								%>
								<tr class="align-middle">
									<td><%=usuario.getUsuario()%></td>
									<td><%=usuario.getNombre()%></td>
									<td><%=usuario.getCorreo()%></td>
									<td><%=usuario.getTelefono()%></td>
									<td><span class="badge rounded-pill bg-info"><%=usuario.getNombreRol()%></span></td>
									<td class="text-end action-buttons">

										<button type="button"
											class="btn btn-sm btn-warning btn-editar"
											data-id="<%=usuario.getIdUsuario()%>"
											data-usuario="<%=usuario.getUsuario()%>"
											data-apellido="<%=usuario.getApellido()%>"
											data-nombre="<%=usuario.getNombre()%>"
											data-correo="<%=usuario.getCorreo()%>"
											data-telefono="<%=usuario.getTelefono()%>"
											data-rol="<%=usuario.getRol()%>">
											<i class="bi bi-pencil"></i>
										</button>

										<button type="button"
											class="btn btn-sm btn-danger btn-eliminar"
											data-id="<%=usuario.getIdUsuario()%>"
											data-nombre="<%=usuario.getNombre()%>">
											<i class="bi bi-trash"></i>
										</button>

									</td>
								</tr>
								<%
								}
								} else {
								%>
								<tr>
									<td colspan="6" class="text-center text-muted py-4">No hay
										usuarios registrados</td>
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
		<jsp:include page="footer.jsp"></jsp:include>
	<!-- Modal Nuevo Usuario -->
	<div class="modal fade" id="nuevoUsuario" tabindex="-1"
		aria-labelledby="nuevoUsuarioLabel" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="nuevoUsuarioLabel">
						<i class="bi bi-person-plus me-2"></i>Nuevo Usuario
					</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				</div>
				
				<form method="POST" action="usuarios" id="registrarUsuarioAdmin">
					<input type="hidden" name="action" value="add">
					
					<div class="modal-body">
						<div class="mb-3">
							<label class="form-label">Nombre completo</label>
							<div class="row g-2">
								<div class="col">
									<input type="text" class="form-control" placeholder="Nombre"
										name="nombre" required>
								</div>
								<div class="col">
									<input type="text" class="form-control" placeholder="Apellido"
										name="apellido" required>
								</div>
							</div>
						</div>

						<div class="mb-3">
							<label class="form-label">Credenciales</label> <input
								type="email" class="form-control mb-2"
								placeholder="Correo electrónico" name="correo" required>
							<div class="row g-2">
								<div class="col">
									<input type="text" class="form-control" placeholder="Usuario"
										name="usuario" required>
								</div>
								<div class="col">
									<input type="password" class="form-control"
										placeholder="Contraseña" name="clave" required>
								</div>
							</div>
						</div>

						<div class="mb-3">
							<label class="form-label">Información de contacto</label> <input
								type="tel" class="form-control" placeholder="Teléfono"
								name="telefono" required>
						</div>

						<div class="mb-3">
							<label class="form-label">Rol del usuario</label> <select
								class="form-select" name="rol" required>
								<%
								for (Rol rol : (LinkedList<Rol>) request.getAttribute("roles")) {
								%>
								<option value="<%=rol.getIdRol()%>"><%=rol.getNombre()%></option>
								<%
								}
								%>
							</select>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary"
							data-bs-dismiss="modal">Cancelar</button>
						<button type="submit" class="btn btn-primary">Guardar
							Usuario</button>
					</div>
				</form>
			</div>
		</div>
	</div>


	<!-- BORRAR USUARIO -->
	<div class="modal fade" id="borrarUsuario">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">Confirmar eliminación</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
				</div>
				<div class="modal-body">
					¿Estás seguro de eliminar al usuario <span id="nombreUsuario"></span>?
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">Cancelar</button>
					<form id="formEliminar" method="POST" action="usuarios">
						<input type="hidden" name="action" value="delete"> <input
							type="hidden" name="idUsuario" id="idUsuario">
						<button type="submit" class="btn btn-danger">Eliminar</button>
					</form>
				</div>
			</div>
		</div>
	</div>

    <!-- MODAL EDITAR USUARIO -->
	<div class="modal fade" id="editarUsuario">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="nuevoUsuarioLabel">
						<i class="bi bi-person-plus me-2"></i>Editar Usuario
					</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<form method="POST" action="usuarios" id="formEditar">
					<input type="hidden" name="action" value="update">
                    <input type="hidden" name="idUsuario" id="editId">
					<div class="modal-body">
						<div class="mb-3">
							<label class="form-label">Nombre completo</label>
							<div class="row g-2">
								<div class="col">
									<input type="text" class="form-control" placeholder="Nombre"
										name="nombre" id="editNombre" required>
								</div>
								<div class="col">
									<input type="text" class="form-control" placeholder="Apellido"
										name="apellido" id="editApellido" required>
								</div>
							</div>
						</div>

						<div class="mb-3">
							<label class="form-label">Credenciales</label> <input
								type="email" class="form-control mb-2"
								placeholder="Correo electrónico" name="correo" id="editCorreo"
								required>
							<div class="row g-2">
								<div class="col">
									<input type="text" class="form-control" placeholder="Usuario"
										name="usuario" id="editUsuario" required>
								</div>
							</div>
						</div>

						<div class="mb-3">
							<label class="form-label">Información de contacto</label> <input
								type="tel" class="form-control" placeholder="Teléfono"
								name="telefono" id="editTelefono" required>
						</div>

						<div class="mb-3">
							<label class="form-label">Rol del usuario</label> <select
								class="form-select" name="rol" id="editRol" required>
								<%
								for (Rol rol : (LinkedList<Rol>) request.getAttribute("roles")) {
								%>
								<option value="<%=rol.getIdRol()%>"><%=rol.getNombre()%></option>
								<%
								}
								%>
							</select>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary"
							data-bs-dismiss="modal">Cancelar</button>
						<button type="submit" class="btn btn-primary">Guardar
							Usuario</button>
					</div>
				</form>
			</div>
		</div>
	</div>


	<script src="js/scriptUsuarios.js"></script>
        <script src="js/notificacionesTiempo.js"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>