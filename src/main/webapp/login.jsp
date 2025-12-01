<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Iniciar Sesión</title>
    <link
            href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
            rel="stylesheet"
            integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
            crossorigin="anonymous">

    <style>
        .text-black {
            color: black !important;
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
<jsp:include page="header.jsp"/>

<div class="container flex-grow-1">
    <div class="row justify-content-center mt-5">
        <div class="col-12 col-md-8 col-lg-6">
            <% String mensaje = (String) session.getAttribute("mensaje");
                if (mensaje != null) { %>
            <div class="alert alert-info alert-dismissible fade show"
                 role="alert">
                <%=mensaje%>
                <button type="button" class="btn-close" data-bs-dismiss="alert"
                        aria-label="Close"></button>
            </div>
            <% session.removeAttribute("mensaje");
            } %>
            <% String errorMensaje = (String) session.getAttribute("error");
                if (errorMensaje != null) { %>
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                <%= errorMensaje %>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <% session.removeAttribute("error");
            } %>

            <div class="card shadow-lg border-0">
                <div class="card-body p-2 p-md-5">
                    <h2 class="text-center mb-4">Iniciar Sesión</h2>
                    <hr>
                    <form action="auth" method="POST">
                        <div class="mb-3">
                            <label for="user" class="form-label">Usuario</label>
                            <input type="text"
                                   class="form-control form-control-lg"
                                   id="user"
                                   name="usuario"
                                   required
                                   placeholder="Ingrese su usuario">
                        </div>

                        <div class="mb-4">
                            <label for="password" class="form-label">Contraseña</label>
                            <div class="input-group">
                                <input type="password"
                                       class="form-control form-control-lg"
                                       id="password"
                                       name="password"
                                       required
                                       placeholder="Ingrese su clave">
                            </div>
                        </div>
                        <input type="hidden" name="action" value="login">
                        <button type="submit" class="btn btn-primary btn-lg w-100 mb-3">
                            Ingresar
                        </button>

                        <div class="d-flex justify-content-between">
                            <a href="recuperarClave.jsp" class="text-decoration-none">¿Olvidaste tu contraseña?</a>
                            <a href="register.jsp" class="text-decoration-none">Registrarse Aquí</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<footer>
    <div class="row align-items-end" style="height: 10vh">
        <div class="col">
            <jsp:include page="footer.jsp"></jsp:include>
        </div>
    </div>
</footer>

<script
        src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
<script src="js/notificacionesTiempo.js"></script>
</body>
</html>

