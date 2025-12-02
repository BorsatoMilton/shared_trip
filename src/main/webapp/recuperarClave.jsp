<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Recuperar Clave</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="styles/recuperarClave.css">
</head>
<body class="d-flex flex-column min-vh-100">
<jsp:include page="WEB-INF/header.jsp"/>

<div class="main-content">
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6">
                <div class="card shadow">
                    <div class="card-header bg-primary text-white">
                        <h4 class="mb-0">Recuperar Contraseña</h4>
                    </div>
                    <div class="card-body">
                        <form method="POST" action="auth">
                            <input type="hidden" name="action" value="recover">
                            <div class="mb-3">
                                <label for="email" class="form-label">Correo Electrónico</label>
                                <input type="email" class="form-control" id="email" name="email" required>
                                <input type="hidden" name="action" value="recuperarClave">
                            </div>
                            <button type="submit" class="btn btn-primary w-100">Recuperar Clave</button>
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

<jsp:include page="WEB-INF/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>