<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Recuperar Contraseña</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="styles/resetearClave.css">
</head>
<body class="d-flex flex-column min-vh-100">
<jsp:include page="header.jsp"/>

<div class="main-content">
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6">
                <div class="card shadow">
                    <div class="card-header bg-primary text-white">
                        <h4 class="mb-0">Recuperar Contraseña</h4>
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

                        <%
                            String token = request.getAttribute("token").toString();
                        %>
                        <form method="POST" action="auth">
                            <input type="hidden" name="action" value="newPassword">
                            <input type="hidden" name="token" value="<%= token %>">
                            <div class="mb-3">
                                <label for="nuevaPassword" class="form-label">Nueva Contraseña</label>
                                <input type="password" class="form-control" id="nuevaPassword"
                                       name="nuevaPassword" required>
                            </div>

                            <div class="mb-3">
                                <label for="confirmarPassword" class="form-label">Confirmar Contraseña</label>
                                <input type="password" class="form-control" id="confirmarPassword"
                                       name="confirmarPassword" required>
                            </div>

                            <button type="submit" class="btn btn-primary w-100">Actualizar Contraseña</button>
                        </form>

                        <div class="text-center mt-3">
                            <a href="login.jsp" class="text-decoration-none">Volver al Login</a>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="js/notificacionesTiempo.js"></script>
</body>
</html>