<style>
    .search-container {
        min-width: 350px;
        margin: 10px auto 10px auto;
    }
    .search-wrapper {
        position: relative;
    }
    .search-wrapper svg {
        position: absolute;
        top: 50%;
        left: 10px;
        transform: translateY(-50%);
        width: 16px;
        height: 16px;
        pointer-events: none;
        color: gray;
    }
    .search-input {
        width: 100%;
        padding: 10px 10px 10px 35px;
        border: 1px solid #ccc;
        border-radius: 8px;
        font-size: 14px;
    }
</style>

<div class="search-container">
    <div class="search-wrapper">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 20">
            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z"></path>
        </svg>

        <input type="text"
               id="buscar"
               class="search-input"
               data-table="tablaPrincipal"
               placeholder="Buscar...">
    </div>
</div>
