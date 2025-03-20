document.addEventListener('DOMContentLoaded', () => {
    initCRUDOperations();
});

const initCRUDOperations = () => {

    document.querySelectorAll('.btn-eliminar').forEach(btn => {
        btn.addEventListener('click', function() {
            abrirModal('borrarUsuario', {
                nombreUsuario: this.dataset.nombre,
                idUsuario: this.dataset.id
            });
        });
    });


    document.querySelectorAll('.btn-editar').forEach(btn => {
        btn.addEventListener('click', function() {
            abrirModal('editarUsuario', {
                editNombre: this.dataset.nombre,
                editApellido: this.dataset.apellido,
                editUsuario: this.dataset.usuario,
                editCorreo: this.dataset.correo,
                editTelefono: this.dataset.telefono,
                editRol: this.dataset.rol,
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