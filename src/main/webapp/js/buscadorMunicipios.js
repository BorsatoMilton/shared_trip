document.addEventListener('DOMContentLoaded', async () => {
	await iniciador();
});

let municipios = [];

function limpiar() {
    document.getElementById("formBusqueda").reset();
}

async function iniciador() {
	try {
		let response = await fetch("resources/municipios.json");
		if (!response.ok) throw new Error(`Error HTTP: ${response.status}`);

		const data = await response.json();
		municipios = data.municipios;

	} catch (error) {
		console.error("Error cargando municipios:", error);
	}
}

const inputOrigen = document.getElementById("origen");
const inputDestino = document.getElementById("destino");
const resultadoBusquedaOrigen = document.getElementById("resultadoCiudadesOrigen");
const resultadoBusquedaDestino = document.getElementById("resultadoCiudadesDestino");

inputOrigen.addEventListener("input", () => {
	cargarOpciones(inputOrigen.value, 'origen');
});

inputDestino.addEventListener("input", () => {
	cargarOpciones(inputDestino.value, 'destino');
});

function cargarOpciones(busqueda, tipoBusqueda) {
	console.log("Lista de municipios:", municipios);

	if (!Array.isArray(municipios)) {
		console.error("Error: 'municipios' no es un array.");
		return;
	}
	tipoBusqueda === 'origen' ? resultadoBusquedaOrigen.innerHTML = "" : resultadoBusquedaDestino.innerHTML = "";

	const resultados = municipios.filter(municipio =>
		municipio.nombre.toLowerCase().includes(busqueda.toLowerCase())
	);

	if (resultados.length === 0) {
		if (tipoBusqueda === 'origen') {
			resultadoBusquedaOrigen.innerHTML = `<div class="dropdown-item text-muted bg-light"> No se encontraron resultados </div>`;
			resultadoBusquedaOrigen.style.display = 'block';
		}
		else {
			resultadoBusquedaDestino.innerHTML = `<div class="dropdown-item text-muted bg-light"> No se encontraron resultados </div>`;
			resultadoBusquedaDestino.style.display = 'block';
		}

		return;
	}

	resultados.forEach((municipio) => {
		const div = document.createElement('div');
		div.className = 'opcion';
		div.textContent = municipio.nombre;
		div.onclick = () => {
			if (tipoBusqueda === 'origen') {
				inputOrigen.value = municipio.nombre;
				resultadoBusquedaOrigen.style.display = 'none'
			}
			else {
				inputDestino.value = municipio.nombre;
				resultadoBusquedaDestino.style.display = 'none'
			}

		};

		tipoBusqueda === 'origen' ? resultadoBusquedaOrigen.appendChild(div) : resultadoBusquedaDestino.appendChild(div)
	});

	tipoBusqueda === 'origen' ? (resultadoBusquedaOrigen.style.display = resultados.length > 0 ? 'block' : 'none') : (resultadoBusquedaDestino.style.display = resultados.length > 0 ? 'block' : 'none')
}
