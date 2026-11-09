# UDITflix: Optimizador de Descarga Masiva de Assets

Reto 3 · Prueba B · Módulo PSP (0490) · 2º DAM

## Descripción del problema

La app móvil de UDITflix tarda en mostrar el login porque descarga varios módulos de uno en uno. Este proyecto simula en Java un motor que los descarga en paralelo con hilos, con prioridades distintas, y que habilita el login solo cuando todos han terminado.

| Módulo | Hilo | Importancia | Prioridad |
|---|---|---|---|
| `catalogo-metadatos` | `DL-catalogo` | Crítico | `Thread.MAX_PRIORITY` (10) |
| `sesion-y-perfil` | `DL-sesion` | Crítico | `Thread.MAX_PRIORITY` (10) |
| `portadas-assets` | `DL-portadas` | Estándar | `Thread.NORM_PRIORITY` (5) |
| `analiticas` | `DL-analitica` | Secundario | `Thread.MIN_PRIORITY` (1) |

Cada módulo simula su descarga con una latencia aleatoria de `1000 + azar.nextInt(3001)` ms.

## Estructura del proyecto

```
src/main/java/es/udit/uditflix/
  Modulo.java          Definición de los módulos (nombre, hilo, prioridad)
  TareaDescarga.java   Runnable de cada descarga y fase de cálculo opcional
  Spinner.java         Runnable del hilo demonio "Cargando…"
  MainUDITflix.java    Crea, lanza y espera los hilos; informe final
docs/ejecuciones/      Salida de cada ejecución y resumen.md
```

Para añadir un módulo basta con añadir una línea al enum `Modulo`.

## Cómo ejecutar

Requiere JDK 17 o superior. No usa librerías externas.

| Argumento | Efecto |
|---|---|
| (ninguno) | Solo la descarga paralela obligatoria |
| `--spinner` | Añade un hilo demonio que imprime `Cargando…` cada 300 ms |
| `--cpu` | Tras la latencia, cada módulo ejecuta un cálculo con un número fijo de iteraciones |

Los argumentos se pueden combinar.

### En IntelliJ IDEA

1. Abrir la carpeta `reto03_uditflix` como proyecto Maven.
2. Ejecutar `es.udit.uditflix.MainUDITflix` con el icono de la clase.
3. Para pasar argumentos: **Run > Edit Configurations… > MainUDITflix > Program arguments**, y escribir por ejemplo `--cpu` o `--spinner --cpu`.

### Por consola

Desde la raíz del proyecto, en `cmd`:

```bat
chcp 65001
javac -encoding UTF-8 -d out src/main/java/es/udit/uditflix/*.java
java -Dfile.encoding=UTF-8 -cp out es.udit.uditflix.MainUDITflix
java -Dfile.encoding=UTF-8 -cp out es.udit.uditflix.MainUDITflix --cpu
java -Dfile.encoding=UTF-8 -cp out es.udit.uditflix.MainUDITflix --spinner
```

`chcp 65001` y `-Dfile.encoding=UTF-8` hacen falta para que se vean bien los acentos y el símbolo `≈` en Windows.

### Limitar la ejecución a un núcleo

Las ejecuciones `cpu-afinidad-*` se lanzaron con este comando, desde `cmd` (no desde PowerShell) y en la raíz del proyecto:

```bat
start "" /b /wait /affinity 1 java -Dfile.encoding=UTF-8 -cp out es.udit.uditflix.MainUDITflix --cpu > docs\ejecuciones\cpu-afinidad-1.txt 2>&1
```

El valor de `/affinity` es una máscara de bits en hexadecimal: `1` deja el proceso solo en el procesador lógico 0. La limitación se puede comprobar en la primera línea de la salida, que muestra `Procesadores disponibles: 1`. No requiere ningún cambio en el código.

## Salida real de ejemplo

Ejecución `sin-flags-1`, sin argumentos:

```
[MAIN] Procesadores disponibles: 12
[MAIN] Descargando 4 módulos en paralelo...
--> [DL-sesion] prio=10 latencia=2454 ms
--> [DL-portadas] prio=5 latencia=2299 ms
--> [DL-analitica] prio=1 latencia=1526 ms
--> [DL-catalogo] prio=10 latencia=3588 ms
<-- [DL-analitica] prio=1 LISTO (duración=1526 ms)
<-- [DL-portadas] prio=5 LISTO (duración=2300 ms)
<-- [DL-sesion] prio=10 LISTO (duración=2454 ms)
<-- [DL-catalogo] prio=10 LISTO (duración=3598 ms)
[MAIN] Todos los módulos listos -> LOGIN HABILITADO
Tiempo total real: ≈ 3600 ms / Duración del más lento: 3598 ms / Suma de todas las latencias: 9867 ms
Comparación: total real ≈ más lento (diferencia 2 ms) y no la suma (diferencia 6267 ms)
Orden de finalización: analiticas -> portadas-assets -> sesion-y-perfil -> catalogo-metadatos
```

## Ejecuciones

Latencias en ms, en este orden: catálogo / sesión / portadas / analítica. La salida completa de cada ejecución está en [`docs/ejecuciones/`](docs/ejecuciones/).

**Sin argumentos**

| Ejecución | Latencias (ms) | Orden de finalización | Total real (ms) |
|---|---|---|---|
| `sin-flags-1` | 3588 / 2454 / 2299 / 1526 | analiticas → portadas-assets → sesion-y-perfil → catalogo-metadatos | 3600 |
| `sin-flags-2` | 3218 / 1352 / 1794 / 2695 | sesion-y-perfil → portadas-assets → analiticas → catalogo-metadatos | 3220 |
| `sin-flags-3` | 2000 / 1184 / 3496 / 3520 | sesion-y-perfil → catalogo-metadatos → portadas-assets → analiticas | 3567 |

**Con `--cpu`**

| Ejecución | Latencias (ms) | Orden de finalización | Total real (ms) |
|---|---|---|---|
| `cpu-1` | 3099 / 3949 / 3467 / 1795 | analiticas → catalogo-metadatos → sesion-y-perfil → portadas-assets | 11500 |
| `cpu-2` | 1224 / 1269 / 2708 / 1500 | catalogo-metadatos → sesion-y-perfil → analiticas → portadas-assets | 9114 |
| `cpu-3` | 3495 / 2270 / 1040 / 2139 | portadas-assets → sesion-y-perfil → analiticas → catalogo-metadatos | 9617 |

**Con `--cpu` limitado a un núcleo**

| Ejecución | Latencias (ms) | Orden de finalización | Total real (ms) |
|---|---|---|---|
| `cpu-afinidad-1` | 3848 / 1494 / 3159 / 3627 | sesion-y-perfil → catalogo-metadatos → portadas-assets → analiticas | 16932 |
| `cpu-afinidad-2` | 3272 / 2922 / 2878 / 3770 | sesion-y-perfil → catalogo-metadatos → portadas-assets → analiticas | 16470 |
| `cpu-afinidad-3` | 3264 / 3192 / 3823 / 1874 | sesion-y-perfil → catalogo-metadatos → portadas-assets → analiticas | 7697 |

### Variabilidad de la fase de cálculo

La fase `--cpu` hace siempre el mismo trabajo (`ITERACIONES_CPU = 1_100_000_000`), y todas las ejecuciones imprimen el mismo resultado. Su duración, en cambio, varía mucho. El valor `cálculo` de la salida es tiempo de reloj, no tiempo de CPU.

| Grupo | Fases de cálculo | Mínimo (ms) | Máximo (ms) |
|---|---|---|---|
| `cpu-*` (12 procesadores) | 12 | 1459 | 7992 |
| `cpu-afinidad-*` (1 procesador) | 12 | 1480 | 12088 |

El objetivo era de unos 1-2 s por módulo, y en el grupo `cpu-*` solo 4 de las 12 fases quedan en ese rango.

En una prueba de diagnóstico aparte, no guardada en `docs/`, `-XX:+PrintCompilation` mostró que `calcular` se compilaba al arrancar el bucle, y la carga del equipo era del 3-10 % antes y después de esa prueba.

> **Hipótesis sin comprobar:** el procesador del equipo (Intel Core i7-1355U) es híbrido, con 2 núcleos de rendimiento y 8 de eficiencia, y de bajo consumo. Según el núcleo en que se ejecute cada hilo y los límites de potencia, el mismo trabajo podría tardar tiempos distintos. No se ha realizado ninguna prueba que confirme o descarte esta causa.

## Análisis del planificador

<!-- TODO(Daniel): redactar el análisis del planificador a partir de las ejecuciones (orden de finalización, prioridades y no determinismo). -->

## Hilo demonio

<!-- TODO(Daniel): explicar el funcionamiento del hilo demonio y por qué el programa termina aunque no acabe. -->

## Metacognición

<!-- TODO(Daniel): redactar las respuestas de metacognición. -->

## Vídeo

<!-- TODO(Daniel): añadir el enlace del vídeo. -->
