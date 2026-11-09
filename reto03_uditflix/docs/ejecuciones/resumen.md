# Resumen de ejecuciones

Latencias en ms, en este orden: catálogo / sesión / portadas / analítica.
Equipo: Intel Core i7-1355U, 12 procesadores lógicos. Salida completa de cada ejecución en su `.txt`.

## Sin flags

| Ejecución | Latencias (ms) | Orden de finalización | Total real (ms) |
|---|---|---|---|
| `sin-flags-1` | 3588 / 2454 / 2299 / 1526 | analiticas → portadas-assets → sesion-y-perfil → catalogo-metadatos | 3600 |
| `sin-flags-2` | 3218 / 1352 / 1794 / 2695 | sesion-y-perfil → portadas-assets → analiticas → catalogo-metadatos | 3220 |
| `sin-flags-3` | 2000 / 1184 / 3496 / 3520 | sesion-y-perfil → catalogo-metadatos → portadas-assets → analiticas | 3567 |

## Con `--cpu`

| Ejecución | Latencias (ms) | Orden de finalización | Total real (ms) |
|---|---|---|---|
| `cpu-1` | 3099 / 3949 / 3467 / 1795 | analiticas → catalogo-metadatos → sesion-y-perfil → portadas-assets | 11500 |
| `cpu-2` | 1224 / 1269 / 2708 / 1500 | catalogo-metadatos → sesion-y-perfil → analiticas → portadas-assets | 9114 |
| `cpu-3` | 3495 / 2270 / 1040 / 2139 | portadas-assets → sesion-y-perfil → analiticas → catalogo-metadatos | 9617 |

## Con `--cpu` limitado a un procesador lógico (extra)

Lanzadas con `start "" /b /wait /affinity 1 java ... --cpu` desde `cmd`. El programa informa de 1 procesador disponible.

| Ejecución | Latencias (ms) | Orden de finalización | Total real (ms) |
|---|---|---|---|
| `cpu-afinidad-1` | 3848 / 1494 / 3159 / 3627 | sesion-y-perfil → catalogo-metadatos → portadas-assets → analiticas | 16932 |
| `cpu-afinidad-2` | 3272 / 2922 / 2878 / 3770 | sesion-y-perfil → catalogo-metadatos → portadas-assets → analiticas | 16470 |
| `cpu-afinidad-3` | 3264 / 3192 / 3823 / 1874 | sesion-y-perfil → catalogo-metadatos → portadas-assets → analiticas | 7697 |
