<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Registro</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
</head>
<body>

<jsp:include page="header.jsp"/>

<div class="container-fluid  d-flex flex-column">
    <div class="row justify-content-center align-items-center flex-grow-1">
        <div class="col-12 col-lg-8 col-xl-6 mt-5">
            <% String mensaje = (String) session.getAttribute("mensaje");
                if (mensaje != null) { %>
            <div class="alert alert-info alert-dismissible fade show" role="alert">
                <%= mensaje %>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <% session.removeAttribute("mensaje");
            } %>
            <% String error = (String) session.getAttribute("error");
                if (error != null) { %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <%= error %>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <% session.removeAttribute("error");
            } %>
            <div class="card shadow-lg border-0 my-5">
                <div class="card-body p-4 p-md-5">
                    <h1 class="text-center mb-4 text-primary fw-bold">
                        <i class="bi bi-person-plus me-2"></i>Registrarse
                    </h1>
                    <hr>

                    <form action="usuarios" method="POST">
                        <input type="hidden" name="action" value="register">
                        <div class="row g-4">

                            <div class="col-md-6">
                                <div class="form-floating">
                                    <input type="text"
                                           class="form-control form-control-lg"
                                           id="nombre"
                                           name="nombre"
                                           placeholder="Nombre"
                                           minlength="2"
                                           maxlength="50"
                                           required
                                           value="<%= session.getAttribute("nombre") != null ? session.getAttribute("nombre") : ""%>">
                                    <label for="nombre" class="form-label">
                                        Nombre
                                    </label>
                                </div>
                            </div>

                            <div class="col-md-6">
                                <div class="form-floating">
                                    <input type="text"
                                           class="form-control form-control-lg"
                                           id="apellido"
                                           name="apellido"
                                           placeholder="Apellido"
                                           minlength="2"
                                           maxlength="50"
                                           required
                                           value="<%= session.getAttribute("apellido") != null ? session.getAttribute("apellido") : "" %>">
                                    <label for="apellido" class="form-label">
                                        Apellido
                                    </label>
                                </div>
                            </div>

                            <div class="col-12">
                                <div class="form-floating">
                                    <input type="email"
                                           class="form-control form-control-lg"
                                           id="correo"
                                           name="correo"
                                           placeholder="Correo Electrónico"
                                           minlength="10"
                                           maxlength="100"
                                           required
                                           value="<%= session.getAttribute("correo") != null ? session.getAttribute("correo") : "" %>">
                                    <label for="correo" class="form-label">
                                        Correo Electrónico
                                    </label>
                                </div>
                            </div>

                            <div class="col-md-6">
                                <div class="form-floating">
                                    <input type="text"
                                           class="form-control form-control-lg"
                                           id="usuario"
                                           name="usuario"
                                           placeholder="Usuario"
                                           required
                                           minlength="3"
                                           maxlength="20"
                                           value="<%= session.getAttribute("usuarioFormRegister") != null ? session.getAttribute("usuarioFormRegister") : "" %>">
                                    <label for="usuario" class="form-label">
                                        Usuario
                                    </label>
                                </div>
                            </div>

                            <div class="col-md-6">
                                <div class="form-floating position-relative">
                                    <input type="password"
                                           class="form-control form-control-lg"
                                           id="clave"
                                           name="clave"
                                           placeholder="Clave"
                                           required
                                           minlength="6"
                                           maxlength="100">
                                    <label for="clave" class="form-label">
                                        Contraseña
                                    </label>
                                </div>
                            </div>

                            <div class="col-12">
                                <div class="form-floating">
                                    <input type="tel"
                                           class="form-control form-control-lg"
                                           id="telefono"
                                           name="telefono"
                                           placeholder="Teléfono"
                                           required
                                           pattern="[0-9]{9,15}"
                                           value="<%= session.getAttribute("telefono") != null ? session.getAttribute("telefono") : "" %>">
                                    <label for="telefono" class="form-label">
                                        Teléfono
                                    </label>
                                </div>
                            </div>

                            <div class="col-12">
                                <button class="btn btn-primary btn-lg w-100 py-3" type="submit">
                                    Registrarse
                                </button>
                                <div class="text-center mt-4">
                                    <p class="text-muted">
                                        ¿Ya tienes cuenta?
                                        <a href="login.jsp" class="text-decoration-none fw-bold">
                                            Inicia Sesión
                                        </a>
                                    </p>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>

<script src="js/notificacionesTiempo.js"></script>

</body>
</html>