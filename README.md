# CompraFacil

Este repositorio contiene dos implementaciones de una sencilla lista de compras:

1. **Aplicación Android** (archivos `.java` y `.xml`).
2. **Aplicación web** ubicada en `webapp/` basada en Flask.

La aplicación web permite:

- Registrar productos con un nombre y precio.
- Marcar productos como comprados.
- Llevar un total de gasto con respecto a un presupuesto fijo de 100.
- Compartir la lista en texto plano desde la ruta `/share`.

## Ejecución de la versión web

1. Instala las dependencias:

```bash
pip install -r webapp/requirements.txt
```

2. Ejecuta la aplicación:

```bash
python webapp/app.py
```

Abre `http://localhost:5000` en tu navegador para usarla.
