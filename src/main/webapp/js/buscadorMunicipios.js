document.addEventListener('DOMContentLoaded', async () => {
    await iniciador();
    inicializarBuscadores();
});

let municipios = [];
let debounceTimer = null;
const stateFocus = {};

async function iniciador() {
    try {
        let response = await fetch("resources/municipios.json");
        if (!response.ok) throw new Error(`Error HTTP: ${response.status}`);
        const data = await response.json();
        municipios = Array.isArray(data.municipios) ? data.municipios : [];
        const hoy = new Date().toISOString().split("T")[0];
        document.querySelectorAll('input[type="date"][id="fecha"]').forEach(i => {
            i.min = hoy;
        });
    } catch (error) {
        console.error("Error cargando municipios:", error);
        municipios = [];
    }
}

function inicializarBuscadores() {
    const buscadores = document.querySelectorAll('.dropdown-container input');
    buscadores.forEach((input, index) => {
        const tipo = input.id && input.id.includes("origen") ? "origen" : "destino";
        const key = tipo + "_" + index;
        input.dataset.key = key;
        const contenedor = input.parentElement.querySelector('.resultadoCiudades');
        if (!contenedor) return;
        contenedor.dataset.key = key;
        stateFocus[key] = -1;
        input.setAttribute('autocomplete', 'off');
        input.addEventListener("input", () => debouncedCargar(input.value, key));
        input.addEventListener("keydown", (e) => manejarTeclas(e, key));
        input.addEventListener("blur", () => setTimeout(() => validarSeleccion(input, key), 150));
    });

    document.addEventListener('click', (e) => {
        if (!e.target.closest('.dropdown-container')) {
            document.querySelectorAll('.resultadoCiudades').forEach(c => {
                c.style.display = 'none';
                c.innerHTML = '';
            });
            Object.keys(stateFocus).forEach(k => stateFocus[k] = -1);
        }
    });

    document.querySelectorAll('form').forEach(form => {
        form.addEventListener('submit', function (e) {
            const inputs = form.querySelectorAll('.dropdown-container input[data-key]');
            const invalid = Array.from(inputs).filter(inp => {
                const val = inp.value.trim();
                return !val || !municipios.some(m => normalizar(m.nombre) === normalizar(val));
            });
            if (invalid.length > 0) {
                e.preventDefault();
                invalid[0].focus();
                alert('Debe seleccionar un origen y destino válidos.');
            }
        });
    });
}

function debouncedCargar(valor, key) {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => cargarOpciones(valor, key), 200);
}

function cargarOpciones(busqueda, key) {
    const contenedor = document.querySelector(`.resultadoCiudades[data-key="${key}"]`);
    const input = document.querySelector(`input[data-key="${key}"]`);
    if (!contenedor || !input) return;
    contenedor.innerHTML = "";
    stateFocus[key] = -1;
    const query = normalizar(busqueda);
    if (!query) {
        ocultarResultados(key);
        return;
    }
    const resultados = municipios.filter(m =>
        normalizar(m.nombre).includes(query)
    );
    if (resultados.length === 0) {
        const noRes = document.createElement('div');
        noRes.className = 'opcion';
        noRes.textContent = 'No se encontraron resultados';
        contenedor.appendChild(noRes);
        contenedor.style.display = 'block';
        return;
    }
    resultados.slice(0, 50).forEach((muni) => {
        const div = document.createElement('div');
        div.className = 'opcion';
        div.textContent = quitarTildesPreservandoCase(muni.nombre);
        div.dataset.value = muni.nombre;
        div.addEventListener('click', () => {
            input.value = muni.nombre;
            ocultarResultados(key);
        });
        div.addEventListener('mouseenter', () => {
            limpiarClaseActiva(contenedor);
            div.classList.add('active');
        });
        contenedor.appendChild(div);
    });
    contenedor.style.display = 'block';
    contenedor.scrollTop = 0;
}

function manejarTeclas(e, key) {
    const contenedor = document.querySelector(`.resultadoCiudades[data-key="${key}"]`);
    if (!contenedor) return;
    const items = Array.from(contenedor.querySelectorAll('.opcion'));
    const max = items.length - 1;
    if (e.key === 'ArrowDown') {
        e.preventDefault();
        if (max < 0) return;
        stateFocus[key] = Math.min(stateFocus[key] + 1, max);
        actualizarFocoVisual(items, stateFocus[key]);
    } else if (e.key === 'ArrowUp') {
        e.preventDefault();
        if (max < 0) return;
        stateFocus[key] = Math.max(stateFocus[key] - 1, 0);
        actualizarFocoVisual(items, stateFocus[key]);
    } else if (e.key === 'Enter') {
        if (stateFocus[key] >= 0 && items[stateFocus[key]]) {
            e.preventDefault();
            const input = document.querySelector(`input[data-key="${key}"]`);
            const seleccionado = items[stateFocus[key]].textContent;
            input.value = seleccionado;
            ocultarResultados(key);
        }
    } else if (e.key === 'Escape') {
        ocultarResultados(key);
        stateFocus[key] = -1;
    }
}

function actualizarFocoVisual(items, index) {
    if (!items || items.length === 0) return;
    limpiarClaseActiva(items[0].parentElement);
    const el = items[index];
    if (el) {
        el.classList.add('active');
        el.scrollIntoView({block: 'nearest'});
    }
}

function limpiarClaseActiva(contenedor) {
    if (!contenedor) return;
    contenedor.querySelectorAll('.opcion.active').forEach(el => {
        el.classList.remove('active');
    });
}

function ocultarResultados(key) {
    const contenedor = document.querySelector(`.resultadoCiudades[data-key="${key}"]`);
    if (contenedor) {
        contenedor.style.display = 'none';
        contenedor.innerHTML = "";
    }
}

function normalizar(texto) {
    return String(texto).toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
}

function quitarTildesPreservandoCase(texto) {
    return String(texto).normalize("NFD").replace(/[\u0300-\u036f]/g, "");
}

function validarSeleccion(input, key) {
    const valor = input.value.trim();
    if (!valor) return;
    const existe = municipios.some(m => normalizar(m.nombre) === normalizar(valor));
    if (!existe) {
        input.value = "";
        ocultarResultados(key);
    }
}
