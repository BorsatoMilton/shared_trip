document.addEventListener('DOMContentLoaded', () => {
    initCRUDOperations();
    
    document.querySelectorAll('.modal').forEach(modal => {
        modal.addEventListener('hide.bs.modal', () => {

            modal.querySelectorAll('button, input, select, textarea, a').forEach(el => {
                el.blur();
            });

            setTimeout(() => {
                if (document.activeElement) {
                    document.activeElement.blur();
                }
                document.body.focus();
            }, 100);
        });

        modal.addEventListener('hidden.bs.modal', () => {
            document.querySelectorAll('.modal-backdrop').forEach(b => b.remove());
            document.body.classList.remove('modal-open');
            document.body.style = '';
        });
    });
});

const initCRUDOperations = () => {
    document.querySelectorAll('.btn-eliminar').forEach(btn => {
        btn.addEventListener('click', function() {
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
                editUsuarioDuenio: this.dataset.usuarioDuenioId,
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
        if (element && (element.tagName === 'INPUT' || element.tagName === 'SELECT')) {
            element.value = data[key];
        } else if (element) {
            element.textContent = data[key];
        }
    });

    const modal = new bootstrap.Modal(modalElement);
    modal.show();
};