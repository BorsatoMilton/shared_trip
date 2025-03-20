<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Registro</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
</head>
<body>

<jsp:include page="header.jsp"/>

<div class="container-fluid  d-flex flex-column">
    <div class="row justify-content-center align-items-center flex-grow-1">
        <div class="col-12 col-lg-8 col-xl-6">
               <% String mensaje = (String) session.getAttribute("mensaje");
                  if (mensaje != null) { %>
                   <div class="alert alert-info alert-dismissible fade show" role="alert">
                       <%= mensaje %>
                       <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                   </div>
               <% session.removeAttribute("mensaje"); } %>
            <div class="card shadow-lg border-0 my-5">
                <div class="card-body p-4 p-md-5">
                    <h1 class="text-center mb-4 text-primary fw-bold">
                        <i class="bi bi-person-plus me-2"></i>Registrarse
                    </h1>
                    <hr>
                    
                    <form action="Register" method="POST">
                        <div class="row g-4">
                            
                            <div class="col-md-6">
                                <div class="form-floating">
                                    <input type="text" 
                                           class="form-control form-control-lg" 
                                           id="nombre" 
                                           name="nombre" 
                                           placeholder="Nombre"
                                           required
                                           >
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
                                           required>
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
                                           placeholder="Correo Electr�nico"
                                           required>
                                    <label for="correo" class="form-label">
                                        Correo Electr�nico
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
                                           minlength="6"
                                           maxlength="20">
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
                                           minlength="6">
                                    <label for="clave" class="form-label">
                                        Contrase�a
                                    </label>
                                </div>
                            </div>

                            <!-- Tel�fono -->
                            <div class="col-12">
                                <div class="form-floating">
                                    <input type="tel" 
                                           class="form-control form-control-lg" 
                                           id="telefono" 
                                           name="telefono" 
                                           placeholder="Tel�fono"
                                           required
                                           pattern="[0-9]{9,15}">
                                    <label for="telefono" class="form-label">
                                        Tel�fono
                                    </label>
                                </div>
                            </div>

                            <div class="col-12">
                                <button class="btn btn-primary btn-lg w-100 py-3" type="submit">
                                    Registrarse
                                </button>
                                <div class="text-center mt-4">
                                    <p class="text-muted">
                                        �Ya tienes cuenta? 
                                        <a href="login.jsp" class="text-decoration-none fw-bold">
                                            Inicia Sesi�n
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





<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>

</body>
</html>