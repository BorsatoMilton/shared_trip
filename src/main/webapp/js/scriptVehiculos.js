document.addEventListener('DOMContentLoaded', () => {
    initCRUDOperations();
});

const initCRUDOperations = () => {

    document.querySelectorAll('.btn-eliminar').forEach(btn => {
        btn.addEventListener('click', function() {
			console.log("ID a eliminar:", this.dataset.id); // Debugging
            abrirModal('borrarVehiculo', {
                idVehiculoEliminar: this.dataset.id
            });
        });
    });
	

    document.querySelectorAll('.btn-editar').forEach(btn => {
        btn.addEventListener('click', function() {
            abrirModal('editarVehiculo', {
                editPatente: this.dataset.patente,
                editModelo: this.dataset.modelo,
                editAnio: this.dataset.anio,
                editUsuarioDuenio: this.dataset.usuario_duenio_id,
                editId: this.dataset.id
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