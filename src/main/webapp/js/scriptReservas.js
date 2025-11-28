document.addEventListener('DOMContentLoaded', () => {
    initCRUDOperations();
});

const initCRUDOperations = () => {


    document.querySelectorAll('.btn-eliminar').forEach(btn => {
        btn.addEventListener('click', function () {
            abrirModal('eliminarReserva', {
                idReservaEliminar: this.dataset.id
            });
        });
    });

    document.querySelectorAll('.btn-cancelar').forEach(btn => {
        btn.addEventListener('click', function () {
            abrirModal('cancelarReserva', {
                idReservaCancelar: this.dataset.id,

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