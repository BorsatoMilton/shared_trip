document.addEventListener('DOMContentLoaded', () => {

    const modalElement = document.getElementById('reservasViaje');
    let modalInstance = null;

    window.reservasAbortController = null;

    document.querySelectorAll('.btn-obtener-reservas').forEach(btn => {
        btn.addEventListener('click', function () {
            const idViaje = this.dataset.id;

            if (!modalInstance) {
                modalInstance = new bootstrap.Modal(modalElement);
            }
            modalInstance.show();

            cargarReservas(idViaje);
        });
    });

    modalElement.addEventListener('hidden.bs.modal', function () {
        if (window.reservasAbortController) {
            try {
                window.reservasAbortController.abort();
            } catch (e) {
            }
            window.reservasAbortController = null;
        }
        resetearModal();
    });
});


async function cargarReservas(idViaje) {

    if (window.reservasAbortController) {
        try {
            window.reservasAbortController.abort();
        } catch (e) {
        }
        window.reservasAbortController = null;
    }

    const controller = new AbortController();
    window.reservasAbortController = controller;
    const signal = controller.signal;

    document.getElementById('reservasLoader').style.display = 'block';
    document.getElementById('reservasContainer').style.display = 'none';
    document.getElementById('sinReservas').style.display = 'none';
    document.getElementById('errorReservas').style.display = 'none';

    const TIMEOUT = 10000;
    const timeoutId = setTimeout(() => {
        try {
            controller.abort();
        } catch (e) {
        }
    }, TIMEOUT);

    try {
        const res = await fetch(`obtenerReservas?idViaje=${encodeURIComponent(idViaje)}`, {
            signal
        });

        clearTimeout(timeoutId);

        if (!res.ok) {
            throw new Error(`HTTP ${res.status}`);
        }

        const data = await res.json();

        if (data.success) {
            if (data.reservas && data.reservas.length > 0) {
                mostrarReservas(data.reservas);
            } else {
                document.getElementById('sinReservas').style.display = 'block';
            }
        } else {
            mostrarError(data.mensaje || 'Error al cargar reservas');
        }

    } catch (error) {

        if (error.name === 'AbortError') {
            mostrarError('La solicitud fue cancelada o tardó demasiado (timeout).');
        } else {
            console.error("Error al cargar reservas:", error);
            mostrarError('Error de conexión al servidor');
        }

    } finally {
        window.reservasAbortController = null;
        document.getElementById('reservasLoader').style.display = 'none';
    }
}

function mostrarReservas(reservas) {
    const container = document.getElementById('reservasContainer');
    container.innerHTML = '';

    reservas.forEach((reserva, index) => {
        const estadoBadge = obtenerBadgeEstado(reserva.estado);
        const pasajero = reserva.pasajero;

        const card = `
            <div class="card mb-3 ${reserva.estado === 'CANCELADA' ? 'border-danger' : ''}">
                <div class="card-body">
                    <div class="row align-items-center">
                        <div class="col-md-1">
                            <div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center" 
                                 style="width: 40px; height: 40px;">
                                <strong>${index + 1}</strong>
                            </div>
                        </div>
                        <div class="col-md-5">
                            <h6 class="mb-1">
                                <i class="bi bi-person-fill text-primary me-1"></i>
                                ${pasajero.nombre} ${pasajero.apellido}
                            </h6>
                            <small class="text-muted">
                                <i class="bi bi-envelope me-1"></i>${pasajero.correo}
                            </small>
                            <br>
                            <small class="text-muted">
                                <i class="bi bi-telephone me-1"></i>${pasajero.telefono || 'N/A'}
                            </small>
                        </div>
                        <div class="col-md-3 text-center">
                            <div class="mb-1">
                                <i class="bi bi-people-fill text-primary"></i>
                                <strong>${reserva.cantidad_pasajeros_reservada}</strong> 
                                ${reserva.cantidad_pasajeros_reservada === 1 ? 'pasajero' : 'pasajeros'}
                            </div>
                            <div>
                                <small class="text-muted">Código: <strong>${reserva.codigo_reserva}</strong></small>
                            </div>
                        </div>
                        <div class="col-md-3 text-end">
                            ${estadoBadge}
                            <br>
                            <small class="text-muted">${reserva.fecha_reserva}</small>
                        </div>
                    </div>
                </div>
            </div>
        `;

        container.insertAdjacentHTML('beforeend', card);
    });

    container.style.display = 'block';
}

function obtenerBadgeEstado(estado) {
    const estados = {
        'EN PROCESO': '<span class="badge bg-warning text-dark"><i class="bi bi-clock me-1"></i>Pendiente</span>',
        'CONFIRMADA': '<span class="badge bg-success"><i class="bi bi-check-circle me-1"></i>Confirmada</span>',
        'CANCELADA': '<span class="badge bg-danger"><i class="bi bi-x-circle me-1"></i>Cancelada</span>',
        'VENCIDA': '<span class="badge bg-secondary"><i class="bi bi-exclamation-octagon me-1"></i>Vencida</span>'
    };

    return estados[estado.toUpperCase()] || '<span class="badge bg-secondary">Desconocido</span>';
}



function mostrarError(mensaje) {
    document.getElementById('errorMensaje').textContent = mensaje;
    document.getElementById('errorReservas').style.display = 'block';
}

function resetearModal() {
    const loader = document.getElementById('reservasLoader');
    const container = document.getElementById('reservasContainer');
    const sinReservas = document.getElementById('sinReservas');
    const errorReservas = document.getElementById('errorReservas');

    if (!loader || !container || !sinReservas || !errorReservas) {
        return;
    }

    loader.style.display = 'block';
    container.style.display = 'none';
    sinReservas.style.display = 'none';
    errorReservas.style.display = 'none';

    container.innerHTML = '';
}