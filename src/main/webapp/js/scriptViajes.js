document.addEventListener('DOMContentLoaded', () => {
    initCRUDOperations();
});

const initCRUDOperations = () => {

    document.querySelectorAll('.btn-codigo').forEach(btn => {
        btn.addEventListener('click', function () {
            abrirModal('introducirCodigo', {
                idViajeAverificar: this.dataset.id
            });
        });
    });


    document.querySelectorAll('.btn-eliminar').forEach(btn => {
        btn.addEventListener('click', function () {
            abrirModal('borrarViaje', {
                idViajeEliminar: this.dataset.id
            });
        });
    });


    document.querySelectorAll('.btn-editar').forEach(btn => {
        btn.addEventListener('click', function () {
            abrirModal('editarViaje', {
                editId: this.dataset.id,
                editFecha: this.dataset.fecha,
                editLugaresDisponibles: this.dataset.lugares_disponibles,
                editOrigen: this.dataset.origen,
                editDestino: this.dataset.destino,
                editPrecioUnitario: this.dataset.precio_unitario,
                editConductor: this.dataset.id_conductor,
                editCancelado: this.dataset.cancelado,
                editLugarSalida: this.dataset.lugar_salida
            });
        });
    });
};


const abrirModal = (modalId, data) => {
    const modalElement = document.getElementById(modalId);
    if (!modalElement) return;


    Object.keys(data).forEach(key => {
        const element = modalElement.querySelector('#' + key);
        if (element) {

            if (element.tagName === 'INPUT' || element.tagName === 'SELECT') {
                element.value = data[key];
            } else {
                element.textContent = data[key];
            }
        }
    });


    const modal = new bootstrap.Modal(modalElement);
    modal.show();
};