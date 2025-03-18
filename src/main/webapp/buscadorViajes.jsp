
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
	crossorigin="anonymous">


<div class="container mt-4 py-3 px-5">
	<form action="buscar" method="get" id="formBusqueda">
		<div class="row">
			<div class="mb-3 col-12 col-md-4">
				<label for="origen" class="form-label">Origen:</label> <input
					type="text" class="form-control" id="origen" name="origen"
					placeholder="Ciudad de origen" required>
					
	
			</div>
			<div class="mb-3 col-12 col-md-4">
				<label for="destino" class="form-label">Destino:</label> <input
					type="text" class="form-control" id="destino" name="destino"
					placeholder="Ciudad de destino" required>
			</div>
			<div class="mb-3 col-12 col-md-4">
				<label for="fecha" class="form-label">Fecha de Viaje:</label> <input
					type="date" class="form-control" id="fecha" name="fecha" required>
			</div>
		</div>
		<div class="d-grid gap-3">
			  <button type="submit" class="btn btn-primary">Buscar</button>
			<button type="button" onclick="limpiar()" class="btn btn-secondary">Limpiar</button>
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


<style>
.container {
	background-color: rgb(166, 164, 164);
	border-radius: 25px;
}

</style>
