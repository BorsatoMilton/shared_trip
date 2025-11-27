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
    <style>
        body {
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f8f9fa !important;
        }

        .main-content {
            flex: 1;
            background-color: #f8f9fa;
        }

        .card {
            border: none;
            transition: all 0.3s ease-in-out;
            overflow: hidden;
            border-radius: 12px;
        }

        .card:hover {
            transform: translateY(-8px);
            box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15) !important;
        }

        .card-body {
            padding: 1.5rem;
        }

        .card-title {
            font-size: 1.3rem;
            font-weight: 700;
            color: #667eea;
            margin-bottom: 1rem;
            text-transform: capitalize;
        }

        .section-header {
            margin-top: 2.5rem;
            margin-bottom: 2rem;
            text-align: center;
            color: #0A58CA;
        }

        .section-header h2 {
            font-size: 2.5rem;
            font-weight: 700;
            text-shadow: 0 8px 12px rgba(0, 0, 0, 0.4);
        }

        .trip-info {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1rem;
            margin-bottom: 1.5rem;
        }

        .info-item {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            font-size: 0.95rem;
            color: #495057;
        }

        .info-item i {
            color: #667eea;
            width: 20px;
            text-align: center;
        }

        .info-item strong {
            color: #212529;
            min-width: 80px;
        }

        .conductor-section {
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            padding: 1rem;
            border-radius: 8px;
            margin-bottom: 1.5rem;
            border-left: 4px solid #667eea;
        }

        .conductor-name {
            font-weight: 600;
            color: #212529;
            margin-bottom: 0.5rem;
            font-size: 0.95rem;
        }

        .rating-container {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            margin-top: 0.5rem;
        }

        .stars {
            color: #ffc107;
            font-size: 0.9rem;
        }

        .rating-text {
            color: #666;
            font-size: 0.85rem;
            font-weight: 500;
        }

        .lugares-badge {
            display: inline-block;
            background: #667eea;
            color: white;
            padding: 0.5rem 1rem;
            border-radius: 20px;
            font-weight: 600;
            font-size: 0.95rem;
            margin-bottom: 1rem;
        }

        .lugares-badge.disponibles {
            background: #28a745;
        }

        .lugares-badge.pocos {
            background: #ffc107;
            color: #333;
        }

        .lugares-badge.lleno {
            background: #dc3545;
        }

        .btn-reserve {
            width: 100%;
            padding: 0.75rem;
            font-weight: 600;
            border-radius: 8px;
            transition: all 0.3s ease;
        }

        .btn-reserve:hover {
            transform: scale(1.02);
        }

        .cantidad-input {
            max-width: 70px;
            text-align: center;
            font-weight: 600;
            border-radius: 6px;
        }

        .input-group {
            gap: 0.5rem;
        }

        .no-viajes {
            text-align: center;
            padding: 4rem 2rem;
            background: linear-gradient(135deg, rgba(255,255,255,0.95) 0%, rgba(248,249,250,0.95) 100%);
            border-radius: 16px;
            margin: 2rem auto;
            max-width: 500px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
        }

        .no-viajes i {
            font-size: 4rem;
            margin-bottom: 1rem;
            color: #667eea;
        }

        .no-viajes h4 {
            color: #212529;
            font-weight: 700;
            margin-bottom: 0.5rem;
        }

        .no-viajes p {
            color: #666;
            margin: 0;
        }

        .alert {
            border-radius: 8px;
            border: none;
            margin-bottom: 1.5rem;
        }

        @media (max-width: 768px) {
            .trip-info {
                grid-template-columns: 1fr;
            }

            .section-header h2 {
                font-size: 1.8rem;
            }

            .no-viajes {
                margin: 1rem;
                padding: 2rem 1rem;
            }
        }
    </style>
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
                                    <form action="reservas" method="post">
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