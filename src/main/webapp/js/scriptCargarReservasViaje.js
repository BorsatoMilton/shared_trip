document.addEventListener('DOMContentLoaded', () => {

    const modalElement = document.getElementById('reservasViaje');
    let modalInstance = null;

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
        resetearModal();
    });
});


function cargarReservas(idViaje) {

    document.getElementById('reservasLoader').style.display = 'block';
    document.getElementById('reservasContainer').style.display = 'none';
    document.getElementById('sinReservas').style.display = 'none';
    document.getElementById('errorReservas').style.display = 'none';


    fetch(`obtenerReservas?idViaje=${idViaje}`)
        .then(response => response.json())
        .then(data => {

            document.getElementById('reservasLoader').style.display = 'none';

            if (data.success) {
                if (data.reservas && data.reservas.length > 0) {
                    mostrarReservas(data.reservas);
                } else {
                    document.getElementById('sinReservas').style.display = 'block';
                }
            } else {
                mostrarError(data.mensaje || 'Error al cargar reservas');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('reservasLoader').style.display = 'none';
            mostrarError('Error de conexión al servidor');
        });
}


function mostrarReservas(reservas) {
    const container = document.getElementById('reservasContainer');
    container.innerHTML = '';

    reservas.forEach((reserva, index) => {
        const estadoBadge = obtenerBadgeEstado(reserva.estado);
        const pasajero = reserva.pasajero;

        const card = `
            <div class="card mb-3 ${reserva.reserva_cancelada ? 'border-danger' : ''}">
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
        'CANCELADA': '<span class="badge bg-danger"><i class="bi bi-x-circle me-1"></i>Cancelada</span>'
    };

    return estados[estado.toUpperCase()] || '<span class="badge bg-secondary">Desconocido</span>';
}


function mostrarError(mensaje) {
    document.getElementById('errorMensaje').textContent = mensaje;
    document.getElementById('errorReservas').style.display = 'block';
}


function resetearModal() {
    document.getElementById('reservasLoader').style.display = 'block';
    document.getElementById('reservasContainer').style.display = 'none';
    document.getElementById('sinReservas').style.display = 'none';
    document.getElementById('errorReservas').style.display = 'none';
    document.getElementById('reservasContainer').innerHTML = '';
}