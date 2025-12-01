<%@ page import="entidades.Viaje" %>
<%@ page import="java.util.LinkedList" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>SharedTrip</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="styles/index.css">
</head>
<body>

<div class="container-fluid p-0">
    <div class="row">
        <div class="col-12">
            <jsp:include page="header.jsp"></jsp:include>
        </div>
    </div>

    <div class="row mt-3">
        <div class="col-12">
            <jsp:include page="buscadorViajes.jsp"></jsp:include>
        </div>
    </div>

    <div class="main-content">
        <div class="row px-3 mt-4">
            <div class="col-12">
                <%
                    String mensaje = (String) session.getAttribute("mensaje");
                    if (mensaje != null) {
                %>
                <div class="alert alert-info alert-dismissible fade show" role="alert">
                    <i class="fas fa-info-circle me-2"></i><%=mensaje%>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <%
                        session.removeAttribute("mensaje");
                    }

                    String error = (String) session.getAttribute("error");
                    if (error != null) {
                %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-circle me-2"></i><%=error%>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <%
                        session.removeAttribute("error");
                    }
                %>
            </div>

            <div class="col-12">
                <div class="section-header">
                    <h2><i class="fas fa-car me-3"></i>Viajes Disponibles</h2>
                </div>
            </div>

            <div class="col-12">
                <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4 mb-5">
                    <%
                        LinkedList<Viaje> viajes = (LinkedList<Viaje>) request.getAttribute("viajes");
                        if (viajes != null && !viajes.isEmpty()) {
                            for (Viaje viaje : viajes) {
                                int lugaresDisponibles = viaje.getLugares_disponibles();
                                String badgeClass = lugaresDisponibles == 0 ? "lleno" : lugaresDisponibles <= 2 ? "pocos" : "disponibles";
                    %>
                    <div class="col">
                        <div class="card h-100 shadow-sm">
                            <div class="card-body d-flex flex-column">

                                <h5 class="card-title">
                                    <i class="fas fa-map-marker-alt me-2"></i><%=viaje.getDestino()%>
                                </h5>

                                <div class="trip-info">
                                    <div class="info-item">
                                        <i class="fas fa-play-circle"></i>
                                        <div>
                                            <strong>Origen:</strong>
                                            <br><%=viaje.getOrigen()%>
                                        </div>
                                    </div>
                                    <div class="info-item">
                                        <i class="fas fa-calendar-alt"></i>
                                        <div>
                                            <strong>Fecha:</strong>
                                            <br><%=viaje.getFecha()%>
                                        </div>
                                    </div>
                                    <div class="info-item">
                                        <i class="fas fa-map-pin"></i>
                                        <div>
                                            <strong>Salida:</strong>
                                            <br><%=viaje.getLugar_salida()%>
                                        </div>
                                    </div>
                                    <div class="info-item">
                                        <i class="fas fa-dollar-sign"></i>
                                        <div>
                                            <strong>Precio:</strong>
                                            <br>$<%=String.format("%.2f", viaje.getPrecio_unitario())%>
                                        </div>
                                    </div>
                                </div>

                                <div class="conductor-section">
                                    <div class="conductor-name">
                                        <i class="fas fa-user-circle me-2" style="color: #667eea;"></i>
                                        <%=viaje.getConductor().getNombre() + " " + viaje.getConductor().getApellido()%>
                                    </div>
                                    <div class="rating-container">
                                        <span class="stars">
                                            <%
                                                double promedio = viaje.getConductor().getPromedio_puntuacion();
                                                int estrellas = (int) Math.round(promedio);
                                                for (int i = 0; i < 5; i++) {
                                                    if (i < estrellas) {
                                            %>
                                            <i class="fas fa-star"></i>
                                            <%
                                            } else {
                                            %>
                                            <i class="far fa-star"></i>
                                            <%
                                                    }
                                                }
                                            %>
                                        </span>
                                        <span class="rating-text">
                                            <%=String.format("%.1f", promedio)%>
                                            (<%=viaje.getConductor().getCantidad_que_puntuaron()%> reseñas)
                                        </span>
                                    </div>
                                </div>

                                <div class="lugares-badge <%=badgeClass%>">
                                    <i class="fas fa-chair me-2"></i>
                                    <%=lugaresDisponibles%> lugar<%=lugaresDisponibles != 1 ? "es" : ""%> disponible<%=lugaresDisponibles != 1 ? "s" : ""%>
                                </div>

                                <div class="mt-auto">
                                    <%
                                        if (lugaresDisponibles > 0) {
                                    %>
                                    <form action="reservas" method="POST">
                                        <input type="hidden" name="action" value="reserve">
                                        <input type="hidden" name="viajeId" value="<%=viaje.getIdViaje()%>">
                                        <div class="input-group mb-3">
                                            <label class="input-group-text" for="cant<%=viaje.getIdViaje()%>">
                                                <i class="fas fa-users me-2"></i>Pasajeros
                                            </label>
                                            <input type="number"
                                                   id="cant<%=viaje.getIdViaje()%>"
                                                   name="cantPasajeros"
                                                   min="1"
                                                   max="<%=lugaresDisponibles%>"
                                                   value="1"
                                                   required
                                                   class="form-control cantidad-input">
                                        </div>
                                        <button type="submit" class="btn btn-success btn-reserve">
                                            <i class="fas fa-check-circle me-2"></i>Reservar
                                        </button>
                                    </form>
                                    <%
                                    } else {
                                    %>
                                    <button class="btn btn-secondary btn-reserve" disabled>
                                        <i class="fas fa-times-circle me-2"></i>Viaje Lleno
                                    </button>
                                    <%
                                        }
                                    %>
                                </div>
                            </div>
                        </div>
                    </div>
                    <%
                        }
                    } else {
                    %>
                    <div class="col-12 d-flex justify-content-center">
                        <div class="no-viajes">
                            <i class="fas fa-search"></i>
                            <h4>No hay viajes disponibles</h4>
                            <p>Intenta ajustar tus criterios de búsqueda</p>
                        </div>
                    </div>
                    <%
                        }
                    %>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="row align-items-end">
    <div class="col">
        <jsp:include page="footer.jsp"></jsp:include>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
<script src="<%= request.getContextPath() %>/js/notificacionesTiempo.js"></script>
</body>
</html>