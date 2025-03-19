document.addEventListener('DOMContentLoaded', () => {
    initCRUDOperations();
});

const initCRUDOperations = () => {

    document.querySelectorAll('.btn-eliminar').forEach(btn => {
        btn.addEventListener('click', function() {
            const modal = new bootstrap.Modal('#borrarUsuario');
            const dataset = this.dataset;
            
            document.getElementById('nombreUsuario').textContent = dataset.nombre;
            document.getElementById('idUsuarioEliminar').value = dataset.id;
            
            modal.show();
        });
    });

    document.querySelectorAll('.btn-editar').forEach(btn => {
        btn.addEventListener('click', function() {
            const modal = new bootstrap.Modal('#editarUsuario');
            const dataset = this.dataset;
            
            
            document.getElementById('editNombre').value = dataset.nombre;
            document.getElementById('editUsuario').value = dataset.usuario;
            document.getElementById('editApellido').value = dataset.apellido;
            document.getElementById('editCorreo').value = dataset.correo;
            document.getElementById('editTelefono').value = dataset.telefono;
            document.getElementById('editRol').value = dataset.rol;
            document.getElementById('editId').value = dataset.id;
            
            modal.show();
        });
    });
};