<%@ page import="entidades.Usuario, entidades.Rol" %>
<div class="container-fluid p-0">
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm sticky-top">
        <div class="container bg-primary">
            <a class="navbar-brand fw-bold fs-3 d-flex align-items-center" href="<%= request.getContextPath() %>/">
                <i class="bi bi-car-front-fill me-2"></i>
                SharedTrip
            </a>
            
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" 
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <% if(request.getSession().getAttribute("usuario") != null) { 
                        Usuario usuario = (Usuario) session.getAttribute("usuario");
                    %>
                        <li class="nav-item">
                            <a class="nav-link" href="<%= request.getContextPath() %>/misViajes">
                                <i class="bi bi-geo-alt me-1"></i>Mis Viajes
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="<%= request.getContextPath() %>/misReservas">
                                <i class="bi bi-journal-check me-1"></i>Mis Reservas
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="<%= request.getContextPath() %>/vehiculos">
                                <i class="bi bi-truck me-1"></i>Mis Vehículos
                            </a>
                        </li>
                        <% if((Integer) usuario.getRol() == 1) { //CHANCHADA %>
                        <li class="nav-item">
                            <a class="nav-link" href="<%= request.getContextPath() %>/usuarios">
                                <i class="bi bi-people-fill me-1"></i>Usuarios
                            </a>
                        </li>
                        <% } %>
                    <% } %>
                </ul>

                <ul class="navbar-nav ms-auto">
                    <% if(session.getAttribute("usuario") == null) { %>
                        <li class="nav-item">
                            <a class="nav-link btn btn-outline-light text-light me-2" href="register.jsp">
                                <i class="bi bi-person-plus me-1"></i>Registrarse
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link btn btn-light text-light" href="login.jsp">
                                <i class="bi bi-box-arrow-in-right me-1"></i>Ingresar
                            </a>
                        </li>
                    <% } else { 
                        Usuario usuario = (Usuario) session.getAttribute("usuario"); %>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" 
                                id="userDropdown" role="button" data-bs-toggle="dropdown" 
                                aria-expanded="false">
                                <div class="me-2">
                                    <i class="bi bi-person-circle fs-5"></i>
                                </div>
                                <div>
                                    <div class="fw-bold"><%= usuario.getNombre() %></div>
                                </div>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end shadow" aria-labelledby="userDropdown">
                                <li>
                                    <a class="dropdown-item" href="<%= request.getContextPath() %>/perfil">
                                        <i class="bi bi-person-lines-fill me-2"></i>Mi Perfil
                                    </a>
                                </li>
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <form action="<%= request.getContextPath() %>/CerrarSesion" method="post">
                                        <button type="submit" class="dropdown-item text-danger">
                                            <i class="bi bi-box-arrow-right me-2"></i>Cerrar Sesión
                                        </button>
                                    </form>
                                </li>
                            </ul>
                        </li>
                    <% } %>
                </ul>
            </div>
        </div>
    </nav>
</div>

<style>
.navbar {
    padding: 0.5rem 0;
    transition: all 0.3s ease;

}

.navbar-brand {
    letter-spacing: 1px;
    transition: transform 0.3s ease;
}

.navbar-brand:hover {
    transform: scale(1.05);
}

.nav-link {
    font-weight: 500;
    transition: all 0.2s ease;
    border-radius: 0.5rem;
    padding: 0.5rem 1rem !important;
}

.nav-link:hover {
    background-color: rgba(255, 255, 255, 0.1);
    transform: translateY(-2px);
}

.dropdown-menu {
    border: none;
    min-width: 220px;
}

.dropdown-item {
    transition: all 0.2s ease;
}

.dropdown-item:hover {
    background-color: #f8f9fa;
    padding-left: 1.5rem;
}

.btn-outline-light:hover {
    background-color: rgba(255, 255, 255, 0.9);
    color: #0d6efd !important;
}
</style>