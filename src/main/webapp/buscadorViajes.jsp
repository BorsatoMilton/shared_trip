<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<head>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="styles/buscadorViajes.css">
</head>

<div class="busqueda-container">
    <h3><i class="fas fa-search"></i>Buscar Viajes</h3>

    <form action="buscar" method="get" id="formBusqueda">
        <div class="row g-4">
            <div class="col-12 col-md-4">
                <label for="origen" class="form-label">
                    <i class="fas fa-play-circle"></i>Origen
                </label>
                <div class="dropdown-container">
                    <input type="text"
                           class="form-control"
                           id="origen"
                           name="origen"
                           placeholder="¿Desde donde?"
                           required
                           autocomplete="off"
                           aria-autocomplete="list"
                           aria-controls="resultadoCiudadesOrigen">
                    <div id="resultadoCiudadesOrigen"
                         class="resultadoCiudades"
                         role="listbox"
                         aria-label="Sugerencias de origen"></div>
                </div>
            </div>

            <div class="col-12 col-md-4">
                <label for="destino" class="form-label">
                    <i class="fas fa-map-marker-alt"></i>Destino
                </label>
                <div class="dropdown-container">
                    <input type="text"
                           class="form-control"
                           id="destino"
                           name="destino"
                           placeholder="¿Hasta donde?"
                           required
                           autocomplete="off"
                           aria-autocomplete="list"
                           aria-controls="resultadoCiudadesDestino">
                    <div id="resultadoCiudadesDestino"
                         class="resultadoCiudades"
                         role="listbox"
                         aria-label="Sugerencias de destino"></div>
                </div>
            </div>


            <div class="col-12 col-md-4">
                <label for="fecha" class="form-label">
                    <i class="fas fa-calendar-alt"></i>Fecha de Viaje
                </label>
                <input type="date"
                       class="form-control"
                       id="fecha"
                       name="fecha">
            </div>
        </div>


        <div class="botones-grupo">
            <a href="<%= request.getContextPath() %>/" class="btn-buscar-limpiar btn-limpiar">
                <i class="fas fa-redo-alt"></i>Limpiar
            </a>
            <button type="submit" class="btn-buscar-limpiar btn-buscar">
                <i class="fas fa-search"></i>Buscar Viajes
            </button>
        </div>
    </form>
</div>

<script src="<%= request.getContextPath() %>/js/buscadorMunicipios.js"></script>