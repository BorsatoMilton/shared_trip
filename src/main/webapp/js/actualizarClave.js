document.addEventListener('DOMContentLoaded', () => {
    initUpdateClaveOperations();
});

const initUpdateClaveOperations = (() =>  {
	console.log("me ejecuto")
	document.querySelectorAll('#btn-update-password').forEach(btn => {
		btn.addEventListener('click', function() {
			abrirModal('actualizarClave', {idUsuario: this.dataset.id})
			
		})
		
	})
	
	
	const abrirModal = (modalId, data) => {
    const modalElement = document.getElementById(modalId);
    if (!modalElement) return;


    Object.keys(data).forEach(key => {
        const element = modalElement.querySelector('#' + key);
        if (element) {

            if (element.tagName === 'INPUT') {
                element.value = data[key];
            } else {
                element.textContent = data[key];
            }
        }
    });
    

    const modal = new bootstrap.Modal(modalElement);
    modal.show();
};
	
})