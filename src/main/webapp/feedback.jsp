<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="entidades.Reserva, entidades.Viaje, entidades.Usuario" %>
<%
    Reserva reserva = (Reserva) request.getAttribute("reserva");
    String token = (String) request.getAttribute("token");
    String error = (String) session.getAttribute("error");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dejá tu feedback - SharedTrip</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
        }

        .header {
            color: #2E86C1;
            border-bottom: 2px solid #2E86C1;
            padding-bottom: 10px;
        }

        .content {
            background-color: #f4f4f4;
            padding: 15px;
            border-radius: 5px;
            margin: 15px auto;
            max-width: 700px;
        }

        .footer {
            color: #7F8C8D;
            margin-top: 20px;
            font-size: 14px;
        }

        .info {
            background-color: #e8f4fd;
            border: 1px solid #b3d9ff;
            padding: 10px;
            border-radius: 5px;
            margin: 10px 0;
        }

        .btn {
            display: inline-block;
            background-color: #2E86C1;
            color: white;
            padding: 10px 15px;
            text-decoration: none;
            border-radius: 5px;
            margin-top: 10px;
            border: none;
            cursor: pointer;
        }

        label {
            display: block;
            margin-top: 8px;
        }

        .error {
            color: #c0392b;
        }

        .rating {
            margin: 10px 0;
        }

        .rating input {
            margin-right: 6px;
        }
    </style>
</head>
<body>
<div class="header">
    <h2>SharedTrip</h2>
    <h3>Contanos cómo fue tu viaje</h3>
</div>

<div class="content">
    <% if (error != null) { %>
    <p class="error"><%= error %>
    </p>
    <% } %>

    <% if (reserva != null) {
        Viaje viaje = reserva.getViaje();
        Usuario pasajero = reserva.getPasajero();
    %>

    <p>Hola <strong><%= pasajero != null ? pasajero.getNombre() : "usuario" %>
    </strong>, gracias por viajar con nosotros.</p>

    <div class="info">
        <strong>Viaje:</strong> <%= (viaje != null ? viaje.getOrigen() + " → " + viaje.getDestino() : "Sin datos") %>
        <br>
        <strong>Fecha:</strong> <%= (viaje != null ? viaje.getFecha() : "") %><br>
        <strong>Conductor:</strong> <%= (viaje != null && viaje.getConductor() != null ? viaje.getConductor().getNombre() + " " + viaje.getConductor().getApellido() : "") %>
    </div>

    <form method="post" action="<%= request.getContextPath() + "/feedback" %>">
        <input type="hidden" name="t" value="<%= token %>"/>
        <label><strong>¿Cómo puntuarías tu experiencia?</strong></label>
        <div class="rating">
            <label><input type="radio" name="puntuacion" value="1"> 1 - Muy mala</label>
            <label><input type="radio" name="puntuacion" value="2"> 2 - Mala</label>
            <label><input type="radio" name="puntuacion" value="3" checked> 3 - Regular</label>
            <label><input type="radio" name="puntuacion" value="4"> 4 - Buena</label>
            <label><input type="radio" name="puntuacion" value="5"> 5 - Excelente</label>
        </div>

        <button type="submit" class="btn">Enviar feedback</button>
    </form>

    <% } else { %>
    <p class="error">Reserva no encontrada. El link puede ser inválido o ya fue usado.</p>
    <% } %>
</div>

<div class="footer">
    <p>Saludos,<br>Equipo SharedTrip</p>
</div>
</body>
</html>
