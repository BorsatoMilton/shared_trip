<%@ page import="entidades.Usuario" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="ISO-8859-1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Actualizar Perfil</title>

    <link
            href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
            rel="stylesheet">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        body {
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        .main-content {
            flex: 1 0 auto;
            width: 100%;
            padding-bottom: 60px;
        }

        .profile-container {
            background: #ffffff;
            border-radius: 15px;
            padding: 2rem;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 500px;
            margin: 2rem auto;
        }

        .profile-container h1 {
            text-align: center;
            margin-bottom: 1.5rem;
            color: #1a73e8;
            font-size: 2rem;
            font-weight: bold;
        }

        .alert {
            width: 500px;
            margin: auto;
        }

    </style>
</head>
<body>
<jsp:include page="header.jsp"></jsp:include>

<div class="main-content">
    <div class="container-fluid p-0">
        <%
            Object usuarioObj = session.getAttribute("usuario");
            if (usuarioObj == null || !(usuarioObj instanceof Usuario usuario)) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

        %>

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
        <div class="profile-container mx-auto">
            <h1>Actualizar Perfil</h1>
            <form method="POST" action="perfil" id="actualizarDatosPerfil">
                <input type="hidden" name="action" value="profile">
                <div class="mb-4">
                    <label class="form-label">Nombre completo</label>
                    <div class="row g-2">
                        <div class="col">
                            <input type="text" class="form-control" placeholder="Nombre"
                                   name="nombre" value="<%= usuario.getNombre() %>" required>
                        </div>
                        <div class="col">
                            <input type="text" class="form-control" placeholder="Apellido"
                                   name="apellido" value="<%= usuario.getApellido() %>" required>
                        </div>
                    </div>
                </div>
                <div class="mb-4">
                    <label class="form-label">Credenciales</label> <input type="email"
                                                                          class="form-control mb-2"
                                                                          placeholder="Correo electrónico"
                                                                          name="correo"
                                                                          value="<%= usuario.getCorreo() %>" required>
                    <input type="text"
                           class="form-control" placeholder="Usuario" name="usuario"
                           value="<%= usuario.getUsuario() %>"
                           required>
                </div>
                <div class="mb-4">
                    <label class="form-label">Información de contacto</label> <input
                        type="tel" class="form-control" placeholder="Teléfono"
                        name="telefono" value="<%= usuario.getTelefono() %>" required>
                </div>
                <div class="d-flex justify-content-end">
                    <button type="button" class="btn btn-warning  me-2" id="btn-update-password"
                            data-id="<%= usuario.getIdUsuario() %>">Actualizar Contraseña
                    </button>
                    <button type="submit" class="btn btn-primary">Guardar
                        Usuario
                    </button>
                </div>
            </form>
        </div>

    </div>
</div>
<jsp:include page="footer.jsp"></jsp:include>


<!-- MODAL -->

<div class="modal fade" id="actualizarClave">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Actualizar Contraseña</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form id="formEditarClave" method="POST" action="perfil">
                <div class="modal-body">
                    <input type="hidden" name="action" value="password">
                    <label class="form-label">Nueva Contraseña</label>
                    <input type="password" class="form-control" placeholder="Ej: irjner231kds" minlength="6"
                           name="clave" id="updateClave" required>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary"
                            data-bs-dismiss="modal">Cancelar
                    </button>
                    <button type="submit" class="btn btn-primary">Actualizar</button>
                </div>
            </form>
        </div>
    </div>
</div>


<script src="js/actualizarClave.js"></script>

<script
        src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="js/notificacionesTiempo.js"></script>
</body>
</html>
