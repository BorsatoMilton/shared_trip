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
    <link rel="stylesheet" href="styles/feedback.css">
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
