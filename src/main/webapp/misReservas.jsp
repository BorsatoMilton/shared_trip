<%@page import="java.time.LocalDateTime"%>
<%@page import="java.sql.Date"%>
<%@ page import="entidades.Reserva"%>
<%@ page import="java.util.LinkedList"%>
<%@ page import="entidades.Viaje" %>
<%@ page import="entidades.Usuario" %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Mis Reservas</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

<style >
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
	<div class="container-fluid p-0"></div>
		<div class="row align-items-start" style="height: 10vh">
			<div class="col">
				<jsp:include page="header.jsp"></jsp:include>
			</div>
	</div>
    
    <div class="main-content">	
			<div class="container-fluid p-0">
	
	
				<main class="container mt-4">
				<div class="card shadow-lg">
					<div
					class="card-header d-flex justify-content-between align-items-center">
					<h3 class="mb-0">
						<i class="bi bi-people-fill me-2"></i>Administraci�n de Reservas
					</h3>
				</div>
					
					<div class="card-body">
					<% 
			        	String mensaje = (String) session.getAttribute("mensaje");
			        	if (mensaje != null) {
		    		%>
					<div class="alert alert-info alert-dismissible fade show"
						role="alert">
						<%=mensaje%>
						<button type="button" class="btn-close" data-bs-dismiss="alert"
							aria-label="Close"></button>
					</div>
					<%
					session.removeAttribute("mensaje");
					}

					String error = (String) session.getAttribute("error");
					if (error != null) {
					%>
					<div class="alert alert-danger alert-dismissible fade show"
						role="alert">
						<%=error%>
						<button type="button" class="btn-close" data-bs-dismiss="alert"
							aria-label="Close"></button>
					</div>
					<%
			            session.removeAttribute("mensaje");
			        	}
		   			 %>
					<div class="scrollable-table">
					<table class="table table-hover table-borderless">
						<thead class="table-light">
							<tr>
								<th scope="col">Destino</th>
								<th scope="col">Fecha de Reserva</th>
								<th scope="col">Fecha de Viaje</th>
								<th scope="col">Cantidad pasajeros</th>
								<th scope="col">Total</th>
								<th scope="col">Cancelado</th>
								<th scope="col" class="text-end">Acciones</th>
							</tr>
						</thead>
						<tbody>
							<%
							 LinkedList<Reserva> reservas = (LinkedList<Reserva>) request.getSession().getAttribute("misreservas");
			                if (reservas != null && !reservas.isEmpty()) {
			                    for (Reserva reserva : reservas) {
		                    	
		            %>
							<tr class="align-middle">
								<td><%= reserva.getViaje().getDestino() %></td>
								<td><%= reserva.getFecha_reserva() %></td>
								<td><%= reserva.getViaje().getFecha() %></td>
								<td><%= reserva.getCantidad_pasajeros_reservada()%></td>
								<td><%= reserva.getViaje().getPrecio_unitario()*reserva.getCantidad_pasajeros_reservada()%></td>
								<td><%= reserva.isReserva_cancelada()? "S�" : "No" %></td>
								<td class="text-end action-buttons">
										
										
										<form action="cancelarReserva" method="post">
										<input type="hidden" name="reservaId"
											value="<%= reserva.getIdReserva() %>">
										
										<input type="hidden" name="viajeId"
											value="<%= reserva.getViaje().getIdViaje() %>">
										<button type="submit" class="btn btn-danger"
											<% if (reserva.isReserva_cancelada()) { %>
											disabled <% } %>>Cancelar</button>
									</form>
									</td>
								
							</tr>
							<%
		                    }
		                } else {
		            %>
							<tr>
								<td colspan="8" class="text-center">No existen reservas.</td>
							</tr>
							<%
		                }
		            %>
						</tbody>
					</table>
					</div>
					</div>
					</div>
					</main>
				</div>
			</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>

<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<footer>
	<div class="row align-items-end" style="height: 10vh">
			<div class="col">
				<jsp:include page="footer.jsp"></jsp:include>
			</div>
		</div>
</footer>
</html>