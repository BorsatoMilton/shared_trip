<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
      rel="stylesheet"
      integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
      crossorigin="anonymous">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<style>
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }

    .busqueda-container {
        background: linear-gradient(135deg, #0D6EFD 0%, #667eea 100%);
        padding: 1rem 2rem;
        margin: 1rem auto;
        border-radius: 20px;
        max-width: 1200px;
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
    }

    .busqueda-container h3 {
        color: white;
        margin-bottom: 2rem;
        text-align: center;
        font-size: 1.8rem;
        font-weight: 700;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
    }

    .busqueda-container h3 i {
        font-size: 2rem;
    }

    .form-label {
        font-weight: 600;
        color: #333;
        margin-bottom: 0.7rem;
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 0.95rem;
    }

    .form-label i {
        color: #667eea;
        font-size: 1.1rem;
    }

    .form-control {
        border: 2px solid #e0e0e0;
        border-radius: 10px;
        padding: 0.75rem 1rem;
        font-size: 0.95rem;
        transition: all 0.3s ease;
        background-color: white;
    }

    .form-control:focus {
        border-color: #667eea;
        box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.15);
        outline: none;
    }

    .form-control:hover {
        border-color: #667eea;
    }

    .dropdown-container {
        position: relative;
        width: 100%;
    }

    .resultadoCiudades {
        position: absolute;
        width: 100%;
        border: none;
        background-color: white;
        max-height: 250px;
        overflow-y: auto;
        z-index: 9999;
        display: none;
        border-radius: 10px;
        box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
        margin-top: 8px;
    }

    .resultadoCiudades::-webkit-scrollbar {
        width: 6px;
    }

    .resultadoCiudades::-webkit-scrollbar-track {
        background: #f1f1f1;
        border-radius: 10px;
    }

    .resultadoCiudades::-webkit-scrollbar-thumb {
        background: #667eea;
        border-radius: 10px;
    }

    .resultadoCiudades::-webkit-scrollbar-thumb:hover {
        background: #764ba2;
    }

    .opcion {
        background-color: white;
        padding: 0.75rem 1rem;
        cursor: pointer;
        line-height: 1.4;
        border-bottom: 1px solid #f0f0f0;
        transition: all 0.2s ease;
        font-size: 0.95rem;
        color: #333;
    }

    .opcion:last-child {
        border-bottom: none;
    }

    .opcion:hover {
        background-color: #f8f9ff;
        padding-left: 1.2rem;
        color: #667eea;
        font-weight: 500;
    }

    .opcion.active {
        background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
        color: white;
        font-weight: 600;
    }

    .resultadoCiudades .no-results {
        padding: 1rem;
        color: #999;
        text-align: center;
        font-size: 0.9rem;
    }

    .botones-grupo {
        display: flex;
        gap: 1rem;
        margin-top: 2rem;
        justify-content: center;
    }

    .btn-buscar-limpiar {
        padding: 0.75rem 2rem;
        border-radius: 10px;
        font-weight: 600;
        border: none;
        transition: all 0.3s ease;
        display: flex;
        align-items: center;
        gap: 0.5rem;
        cursor: pointer;
        font-size: 0.95rem;
    }

    .btn-limpiar {
        background-color: white;
        color: #667eea;
        min-width: 150px;
    }

    .btn-limpiar:hover {
        background-color: #f0f7ff;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
    }

    .btn-buscar {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        min-width: 150px;
    }

    .btn-buscar:hover {
        color: white;
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3);
    }

    .mb-3 {
        margin-bottom: 1.5rem !important;
    }

    @media (max-width: 768px) {
        .busqueda-container {
            padding: 2rem 1.5rem;
            margin: 1.5rem 1rem;
        }

        .busqueda-container h3 {
            font-size: 1.4rem;
            margin-bottom: 1.5rem;
        }

        .botones-grupo {
            flex-direction: column;
        }

        .btn-buscar-limpiar {
            width: 100%;
            justify-content: center;
        }

        .row {
            margin: 0 !important;
        }

        .col-12 {
            padding: 0 !important;
        }
    }

    @media (max-width: 576px) {
        .busqueda-container {
            padding: 1.5rem 1rem;
        }

        .busqueda-container h3 {
            font-size: 1.2rem;
        }

        .form-label {
            font-size: 0.85rem;
        }

        .form-control {
            padding: 0.65rem 0.8rem;
            font-size: 0.9rem;
        }
    }
</style>

<div class="busqueda-container">
    <h3><i class="fas fa-search"></i>Buscar Viajes</h3>

    <form action="buscar" method="get" id="formBusqueda">
        <div class="row g-4">
            <!-- Origen -->
            <div class="col-12 col-md-4">
                <label for="origen" class="form-label">
                    <i class="fas fa-play-circle"></i>Origen
                </label>
                <div class="dropdown-container">
                    <input type="text"
                           class="form-control"
                           id="origen"
                           name="origen"
                           placeholder="¿De donde sales?"
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

            <!-- Destino -->
            <div class="col-12 col-md-4">
                <label for="destino" class="form-label">
                    <i class="fas fa-map-marker-alt"></i>Destino
                </label>
                <div class="dropdown-container">
                    <input type="text"
                           class="form-control"
                           id="destino"
                           name="destino"
                           placeholder="¿A donde vas?"
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

            <!-- Fecha -->
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

        <!-- Botones -->
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

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous">
</script>

<script src="<%= request.getContextPath() %>/js/buscadorMunicipios.js"></script>