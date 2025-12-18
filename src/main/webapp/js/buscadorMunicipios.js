document.addEventListener('DOMContentLoaded', async () => {
    await loadMunicipios();
    initSearchers();
});

let municipios = [];
const debounceTimers = {};
const stateFocus = {};
const justSelected = {};

async function loadMunicipios() {
    try {
        const response = await fetch("resources/municipios.json");
        if (!response.ok) throw new Error(`HTTP Error: ${response.status}`);
        const data = await response.json();
        municipios = Array.isArray(data.municipios) ? data.municipios : [];

        const minDate = new Date().toISOString().split("T")[0];
        document.getElementById("fecha").min = minDate;
    } catch (error) {
        console.error("Error loading municipios:", error);
    }
}

function initSearchers() {
    const inputs = document.querySelectorAll('.dropdown-container input');

    inputs.forEach((input, index) => {
        const key = (input.id.includes("origen") ? "origen" : "destino") + "_" + index;
        const resultsContainer = input.parentElement.querySelector('.resultadoCiudades');

        if (!resultsContainer) return;

        input.dataset.key = key;
        resultsContainer.dataset.key = key;
        stateFocus[key] = -1;
        input.autocomplete = "off";

        input.addEventListener("input", () => {
            input.removeAttribute('data-selected');
            debounce(key, () => loadOptions(input.value, key), 200);
        });

        input.addEventListener("keydown", (e) => handleKeys(e, key));
        input.addEventListener("blur", () =>
            setTimeout(() => validateSelection(input, key), 150)
        );
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
        form.addEventListener('submit', (e) => {
            const inputs = form.querySelectorAll('.dropdown-container input[data-key]');
            const invalid = Array.from(inputs).some(inp => {
                const val = inp.value.trim();
                return !val || !municipios.some(m => normalize(m.nombre) === normalize(val));
            });

            if (invalid) {
                e.preventDefault();
                Array.from(inputs).forEach(inp => {
                    if (!inp.value.trim() || !municipios.some(m => normalize(m.nombre) === normalize(inp.value))) {
                        inp.value = "";
                        inp.focus();
                    }
                });
                alert('Debe seleccionar un origen y destino válidos.');
            }
        });
    });
}

function debounce(key, callback, delay) {
    clearTimeout(debounceTimers[key]);
    debounceTimers[key] = setTimeout(callback, delay);
}

function loadOptions(search, key) {
    const container = document.querySelector(`.resultadoCiudades[data-key="${key}"]`);
    const input = document.querySelector(`input[data-key="${key}"]`);
    if (!container || !input) return;

    container.innerHTML = "";
    stateFocus[key] = -1;

    const query = normalize(search);
    if (!query) {
        hideResults(key);
        return;
    }

    const results = municipios.filter(m => normalize(m.nombre).includes(query)).slice(0, 50);

    if (results.length === 0) {
        const noRes = document.createElement('div');
        noRes.className = 'opcion no-results';
        noRes.textContent = 'No se encontraron resultados';
        container.appendChild(noRes);
        container.style.display = 'block';
        return;
    }

    results.forEach((muni) => {
        const div = document.createElement('div');
        div.className = 'opcion';
        div.textContent = `${muni.nombre}, ${muni.provincia?.nombre ?? ""}`.trim();
        div.dataset.value = muni.nombre;

        div.addEventListener('click', () => {
            input.value = muni.nombre;
            input.setAttribute('data-selected', 'true');
            hideResults(key);
        });

        div.addEventListener('mouseenter', () => {
            clearActiveClass(container);
            div.classList.add('active');
            stateFocus[key] = Array.from(container.querySelectorAll('.opcion')).indexOf(div);
        });

        container.appendChild(div);
    });

    container.style.display = 'block';
    container.scrollTop = 0;
}

function handleKeys(e, key) {
    const container = document.querySelector(`.resultadoCiudades[data-key="${key}"]`);
    if (!container) return;

    const items = Array.from(container.querySelectorAll('.opcion'));
    const max = items.length - 1;

    switch (e.key) {
        case 'ArrowDown':
            e.preventDefault();
            if (max >= 0) {
                stateFocus[key] = Math.min(stateFocus[key] + 1, max);
                updateFocus(items, stateFocus[key]);
            }
            break;
        case 'ArrowUp':
            e.preventDefault();
            if (max >= 0) {
                stateFocus[key] = Math.max(stateFocus[key] - 1, 0);
                updateFocus(items, stateFocus[key]);
            }
            break;
        case 'Enter':
            if (stateFocus[key] >= 0 && items[stateFocus[key]]) {
                e.preventDefault();
                const input = document.querySelector(`input[data-key="${key}"]`);
                input.value = items[stateFocus[key]].dataset.value;
                input.setAttribute('data-selected', 'true');
                justSelected[key] = true;
                hideResults(key);
            }
            break;
        case 'Escape':
            hideResults(key);
            stateFocus[key] = -1;
    }
}

function updateFocus(items, index) {
    if (!items.length) return;
    clearActiveClass(items[0].parentElement);
    if (items[index]) {
        items[index].classList.add('active');
        items[index].scrollIntoView({ block: 'nearest' });
    }
}

function clearActiveClass(container) {
    if (container) {
        container.querySelectorAll('.opcion.active').forEach(el => el.classList.remove('active'));
    }
}

function hideResults(key) {
    const container = document.querySelector(`.resultadoCiudades[data-key="${key}"]`);
    if (container) {
        container.style.display = 'none';
        container.innerHTML = "";
    }
}

function normalize(text) {
    return String(text || '')
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .replace(/ñ/g, "n");
}

function validateSelection(input, key) {
    if (justSelected[key]) {
        justSelected[key] = false;
        return;
    }

    const valor = input.value.trim();
    if (!valor) return;

    const exists = municipios.some(m => normalize(m.nombre) === normalize(valor));

    if (!exists) {
        input.value = "";
        input.removeAttribute('data-selected');
    } else {
        input.setAttribute('data-selected', 'true');
    }

    hideResults(key);
}