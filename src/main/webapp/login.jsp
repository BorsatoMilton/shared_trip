<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Iniciar Sesi�n</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
	crossorigin="anonymous">
</head>

<body>
 <jsp:include page="header.jsp"/>

    <div class="container flex-grow-1">
        <div class="row justify-content-center mt-5">
            <div class="col-12 col-md-8 col-lg-6">
                <% String mensaje = (String) session.getAttribute("errorMessage"); 
                   if (mensaje != null) { %>
                    <div class="alert alert-warning alert-dismissible fade show" role="alert">
                        <%= mensaje %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                <% session.removeAttribute("errorMessage"); } %>
                
                <div class="card shadow-lg border-0">
                    <div class="card-body p-2 p-md-5">
                        <h2 class="text-center mb-4">Iniciar Sesi�n</h2>
                        <hr>
                        <form action="signin" method="POST">
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
                                <label for="password" class="form-label">Contrase�a</label>
                                <div class="input-group">
                                    <input type="password" 
                                           class="form-control form-control-lg" 
                                           id="password" 
                                           name="password" 
                                           required
                                           placeholder="Ingrese su clave">
                                </div>
                            </div>
                            
                            <button type="submit" class="btn btn-primary btn-lg w-100 mb-3">
                                Ingresar
                            </button>
                            
                            <div class="d-flex justify-content-between">
                                <a href="#" class="text-decoration-none">�Olvidaste tu contrase�a?</a>
                                <a href="register.jsp" class="text-decoration-none">Registrarse Aqu�</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="footer.jsp"/>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
		integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
		crossorigin="anonymous"></script>

</body>
</html>

