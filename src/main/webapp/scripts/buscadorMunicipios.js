/**
 
 */
let municipios = [];

// Cargar municipios desde el archivo JSON
fetch("../resources/municipios.json")
    .then(response => response.json())
    .then(data => municipios = data.nombre)
    .catch(error => console.error("Error cargando municipios:", error));

const inputOrigen = document.getElementById("origen");

inputOrigen.addEventListener("keypress", (event)=> {const termino = event}
);