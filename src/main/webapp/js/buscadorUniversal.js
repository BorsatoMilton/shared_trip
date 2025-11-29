document.getElementById("buscar").addEventListener("input", function () {
    let filtro = this.value.toLowerCase();
    let tablaId = this.getAttribute("data-table");
    let filas = document.querySelectorAll(`#${tablaId} tbody tr:not(#noResultados)`);
    let coincidencias = 0;

    filas.forEach(fila => {
        let texto = fila.textContent.toLowerCase();
        if (texto.includes(filtro)) {
            fila.style.display = "";
            coincidencias++;
        } else {
            fila.style.display = "none";
        }
    });

    let mensaje = document.getElementById("noResultados");
    mensaje.style.display = (coincidencias === 0) ? "block" : "none";
});


