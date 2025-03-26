
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
	crossorigin="anonymous">


<div class="container mt-4 py-3 px-5">
	<form action="buscar" method="get" id="formBusqueda">
		<div class="row">
		
			<div class="mb-3 col-12 col-md-4">
				<label for="origen" class="form-label">Origen:</label> 
				<div class="dropdown-container">
					<input type="text" class="form-control" id="origen" name="origen" placeholder="Ciudad de origen" required>
					<div id="resultadoCiudadesOrigen" class="resultadoCiudades"></div>
				</div>
	
			</div>
			
			<div class="mb-3 col-12 col-md-4">
				<label for="destino" class="form-label">Destino:</label> 
				<div class="dropdown-container">
					<input type="text" class="form-control" id="destino" name="destino" placeholder="Ciudad de destino" required>
					<div id="resultadoCiudadesDestino" class="resultadoCiudades"></div>
				</div>
			</div>
			
			<div class="mb-3 col-12 col-md-4">
				<label for="fecha" class="form-label">Fecha de Viaje:</label> <input
					type="date" class="form-control" id="fecha" name="fecha">
			</div>
			
		</div>
		<div class="d-flex justify-content-between">
			<button type="button" onclick="limpiar()" class="btn btn-secondary buscar-limpiar me-3">Limpiar</button>
			  <button type="submit" class="btn btn-primary buscar-limpiar ms-3">Buscar</button>
		</div> 
	</form>
</div>
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
	integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
	crossorigin="anonymous">

</script>

<script>
    function limpiar() {
        document.getElementById("formBusqueda").reset();
        sessionStorage.removeItem("viajesCargados"); // Para que se vuelva a cargar en index.jsp
        window.location.href = "ViajesListado?reset=true"; 
    }
    
    
</script>

<script src = "js/buscadorMunicipios.js">
	
</script>

<style>
.container {
	background-color: rgb(166, 164, 164);
	border-radius: 25px;
	
}

.opcion{
	background-color: white;
	height: 30px;
	padding: 0 0 8px 8px;
	cursor:pointer; 
}

.opcion:hover {
	background-color: #3B71CA;
}

.resultadoCiudades {
    position: relative;  /* Ahora empuja los elementos hacia abajo */
    width: 100%;
    border: 1px solid #ccc;
    background-color: white;
    max-height: 200px;
    overflow-y: auto;
    z-index: 10;
    display: none;
    border-radius: 5px;
    box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
    margin-top: 5px;
}

/* Asegurar que los botones estén debajo del dropdown */
.buscar-limpiar-container {
    margin-top: 10px;  /* Ajusta la separación */
    display: flex;
    justify-content: space-between;
    width: 100%;
}

/* Botones con tamaño completo */
.buscar-limpiar {
    width: 48%;
}

.dropdown-container {
    position: relative;
    width: 100%;
    display: flex;
    flex-direction: column;
}



</style>
