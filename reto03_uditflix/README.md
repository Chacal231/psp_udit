# Reto 3 · Motor Concurrente en UDITflix

**Módulo:** 0490 · Programación de Servicios y Procesos (2º DAM)

**RA vinculado:** RA2 · Programación de aplicaciones multihilo

**Autor/a:** Daniel Baeza

**Prueba realizada:** ☐ A · El Pipeline de Transcodificación de Tráilers  ☒ B · El Optimizador de Descarga Masiva de Assets

**Repositorio:** https://github.com/Chacal231/psp_udit.git

**Vídeo de defensa técnica (< 3 min):** https://365udit-my.sharepoint.com/:v:/g/personal/daniel_baeza_alumfp_udit_es/IQBOq2xpvcoiQLEXwVa6qFwBATQaFFJg9MdFcRt8CuAa1Fw?nav=eyJyZWZlcnJhbEluZm8iOnsicmVmZXJyYWxBcHAiOiJPbmVEcml2ZUZvckJ1c2luZXNzIiwicmVmZXJyYWxBcHBQbGF0Zm9ybSI6IldlYiIsInJlZmVycmFsTW9kZSI6InZpZXciLCJyZWZlcnJhbFZpZXciOiJNeUZpbGVzTGlua0NvcHkifX0&e=lMJ9dc

---

## 📺 1. Resumen del problema

La app móvil de UDITflix tarda en mostrar el login porque descarga sus módulos de uno en uno. El usuario espera la suma de todas las descargas, aunque esas descargas no dependen entre sí.

La solución simula en Java un motor que descarga los cuatro módulos a la vez, cada uno en su propio hilo. Cada hilo tiene una prioridad según la importancia del módulo. El login se habilita solo cuando han terminado todos.

| Módulo | Hilo | Importancia | Prioridad |
|---|---|---|---|
| `catalogo-metadatos` | `DL-catalogo` | Crítico | `Thread.MAX_PRIORITY` (10) |
| `sesion-y-perfil` | `DL-sesion` | Crítico | `Thread.MAX_PRIORITY` (10) |
| `portadas-assets` | `DL-portadas` | Estándar | `Thread.NORM_PRIORITY` (5) |
| `analiticas` | `DL-analitica` | Secundario | `Thread.MIN_PRIORITY` (1) |

Cada descarga se simula con `Thread.sleep()` y una latencia aleatoria de `1000 + azar.nextInt(3001)` ms, es decir, entre 1000 y 4000 ms.

---
## 🧠 2. Antes de empezar: planifico (5 min, sin tocar el teclado)

Responde **antes** de escribir una sola línea de código. No importa si te equivocas: importa dejar escrito qué pensabas.

- **¿Qué parte de la base v0 de clase voy a reutilizar tal cual?** el main con hilos
- **¿Qué es nuevo y me da más respeto?** las latencias aleatorias
- **Mi plan en 4-5 pasos, en orden:**
    1. Adaptar la clase de tarea de la base v0 para simular una descarga con una latencia aleatoria.
    2. Definir los cuatro módulos con su prioridad y crear un hilo con nombre propio para cada uno.
    3. Guardar los hilos en un `ArrayList<Thread>` y lanzarlos con un bucle de `start()`, esperándolos con otro bucle de `join()`.
    4. Habilitar el login solo al final e imprimir el tiempo total, el módulo más lento y la suma de latencias.
    5. Ejecutar varias veces, anotar el orden de finalización y analizar el efecto de las prioridades.

### Predicciones (comprueba al final si acertaste)

| Pregunta | Mi predicción                                                                                                                            |
|---|------------------------------------------------------------------------------------------------------------------------------------------|
| ¿Cuánto tardará el programa si los 4 hilos van **a la vez**? | 4000 ms (el módulo más lento)                                                                                                            |
| ¿Y si se ejecutaran **uno detrás de otro**? | 9000 ms (1500 + 2500 + 4000 + 1000)                                                                                                      |
| ¿Saldrán los hilos siempre en el mismo orden? ¿Por qué? | Terminarán siempre en el mismo orden porque las duraciones son fijas; el orden de inicio podría variar porque lo decide el planificador. |
| *(Solo Prueba B)* ¿Terminarán antes los hilos de prioridad alta? | No lo sé                                                                                                                                 |
| ¿El tiempo total se parecerá a la duración del hilo más lento o a la suma? | Al más lento.                                                                                                                            |

---

## 🏗️ 3. Arquitectura de la solución

### 3.1 Esquema

```
Modulo (enum) ──► define nombre, nombre de hilo y prioridad de cada módulo
      │
      ▼
MainUDITflix (hilo main)
      │  crea una TareaDescarga (Runnable) y un Thread por módulo
      │  setPriority() y guarda cada hilo en ArrayList<Thread>
      │
      ├──start()──► DL-catalogo  ─┐
      ├──start()──► DL-sesion    ─┤  cada TareaDescarga registra su fin en
      ├──start()──► DL-portadas  ─┤  una ConcurrentLinkedQueue compartida
      └──start()──► DL-analitica ─┘
                                  │
      main: bucle de join() ◄─────┘
      │
      ▼
LOGIN HABILITADO + tiempos + orden de finalización

Opcional con --spinner: main ──start()──► spinner (hilo demonio, Spinner)
```

### 3.2 Componentes y conceptos

| Componente / concepto | Uso en este proyecto | Explicación sencilla |
|---|---|---|
| `Runnable` | `TareaDescarga` y `Spinner` lo implementan | Describe el trabajo que hay que hacer, pero no lo ejecuta por sí mismo. |
| `Thread` | Un objeto por módulo, con nombre propio | Es la línea de ejecución real que lleva a cabo ese trabajo. |
| `start()` | Bucle de lanzamiento en `MainUDITflix` | Crea el hilo y vuelve enseguida; `main` no espera. |
| `run()` | Contiene la descarga simulada | Es el código que ejecuta el hilo. Llamarlo directamente no crea ningún hilo. |
| `join()` | Bucle de espera en `MainUDITflix` | Hace que `main` se quede esperando hasta que ese hilo termine. |
| `getState()` | No se usa en este proyecto | Permite consultar el estado de un hilo: `NEW`, `RUNNABLE`, `TIMED_WAITING`, `TERMINATED`… |
| `Thread.sleep()` | Simula la latencia de red | El hilo se duerme ese tiempo y no consume CPU mientras tanto. |
| `setPriority()` | Se llama antes de `start()`, con el valor del enum | Es una sugerencia al planificador, no una orden. |
| `setDaemon()` | `setDaemon(true)` en el hilo del spinner | Un hilo demonio no mantiene vivo el programa. |

Otras decisiones del código:

- La cola `ConcurrentLinkedQueue` guarda el orden de finalización porque varios hilos escriben en ella a la vez.
- `latenciaMs` y `duracionMs` son campos normales. `main` solo los lee después de `join()`, y `join()` garantiza que ve sus valores finales.
- Si llega una `InterruptedException`, se restaura el flag con `Thread.currentThread().interrupt()` y el hilo sale. En ese caso no se habilita el login.

### 3.3 Hilos del programa

| Hilo | Qué hace |
|---|---|
| `main` | Crea los hilos, los lanza, espera a que terminen e imprime el informe. |
| `DL-catalogo` | Descarga simulada de `catalogo-metadatos`. |
| `DL-sesion` | Descarga simulada de `sesion-y-perfil`. |
| `DL-portadas` | Descarga simulada de `portadas-assets`. |
| `DL-analitica` | Descarga simulada de `analiticas`. |
| `spinner` | Solo con `--spinner`: imprime `Cargando…` cada 300 ms. |

Con `--cpu`, cada hilo `DL-*` ejecuta además, tras la latencia, un cálculo de `ITERACIONES_CPU = 1_100_000_000` iteraciones.

---


## 🚀 4. Cómo ejecutar el proyecto

- **Clase principal:** `es.udit.uditflix.MainUDITflix`
- **Versión de Java:** JDK 17 (Eclipse Adoptium)
- **Equipo de las pruebas:** Windows 11, Intel Core i7-1355U, 12 procesadores lógicos
- **Archivos externos necesarios:** ninguno

| Argumento | Efecto |
|---|---|
| (ninguno) | Solo la descarga paralela obligatoria |
| `--spinner` | Añade un hilo demonio que imprime `Cargando…` cada 300 ms |
| `--cpu` | Tras la latencia, cada módulo ejecuta un cálculo con un número fijo de iteraciones |

Los argumentos se pueden combinar.

### En IntelliJ IDEA

1. Clonar el repositorio y abrir la carpeta `reto03_uditflix` como proyecto Maven.
2. Esperar a que el proyecto indexe.
3. Ejecutar `es.udit.uditflix.MainUDITflix`.
4. Para pasar argumentos: **Run > Edit Configurations… > MainUDITflix > Program arguments**, y escribir por ejemplo `--cpu` o `--spinner --cpu`.

### Por consola

Desde la raíz del proyecto, en `cmd`:

```bat
chcp 65001
javac -encoding UTF-8 -d out src/main/java/es/udit/uditflix/*.java
java -Dfile.encoding=UTF-8 -cp out es.udit.uditflix.MainUDITflix
java -Dfile.encoding=UTF-8 -cp out es.udit.uditflix.MainUDITflix --cpu
java -Dfile.encoding=UTF-8 -cp out es.udit.uditflix.MainUDITflix --spinner
```

### Limitar la ejecución a un núcleo

Las ejecuciones `cpu-afinidad-*` se lanzaron así, desde `cmd` (no desde PowerShell) y en la raíz del proyecto:

```bat
start "" /b /wait /affinity 1 java -Dfile.encoding=UTF-8 -cp out es.udit.uditflix.MainUDITflix --cpu > docs\ejecuciones\cpu-afinidad-1.txt 2>&1
```

El valor de `/affinity` es una máscara de bits en hexadecimal: `1` deja el proceso solo en el procesador lógico 0. La primera línea de la salida lo confirma con `Procesadores disponibles: 1`. No hace falta cambiar el código.

---

## 🔍 5. Notas técnicas: problemas reales encontrados

| Situación | Qué ocurrió | Qué se hizo |
|---|---|---|
| Ajustar la fase `--cpu` a unos 1-2 s | En pruebas de calibración no guardadas en `docs/`, 1.500 millones de iteraciones tardaron de 3785 a 4637 ms y 550 millones de 718 a 828 ms. | Se fijó `ITERACIONES_CPU = 1_100_000_000`. El número es constante: la cantidad de trabajo no depende del tiempo. |
| Duración irregular del cálculo | Con el mismo trabajo y el mismo resultado, las fases de cálculo tardan de 1459 a 7992 ms en el grupo `cpu-*`. | Se descartaron dos causas en una prueba no guardada en `docs/`: la compilación JIT (`-XX:+PrintCompilation` mostró `calcular` compilado al arrancar el bucle) y la carga del equipo (3-10 % antes y después). La causa real no se ha determinado. Ver 5.3. |
| Evitar que el JIT elimine el cálculo | Un cálculo cuyo resultado no se usa podría eliminarse al optimizar. | Se imprime `resultado=...` en cada módulo. |
| Comparar tiempos con `--cpu` | La suma de latencias no incluye el cálculo: en `cpu-2` es de 6701 ms, menos que el total real de 9114 ms. | Con `--cpu` se imprime también la suma de duraciones reales y la comparación usa esa suma. |
| Codificación en Windows | La codificación por defecto del sistema era Cp1252, que no incluye el símbolo `≈`. | Se ejecuta con `-Dfile.encoding=UTF-8`, y en consola se usa `chcp 65001`. La salida a archivo se redirige desde `cmd`. |
| Competencia real por la CPU | Con 12 procesadores lógicos, 4 hilos no tienen por qué competir entre sí. | Se añadieron 3 ejecuciones con `--cpu` limitadas a un procesador lógico. |

---

## 📊 6. Evidencias de ejecución

### 6.1 Trazas de consola

Sin argumentos (`sin-flags-1`):

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

Con `--cpu` (`cpu-3`):

```
[MAIN] Procesadores disponibles: 12
[MAIN] Descargando 4 módulos en paralelo...
--> [DL-catalogo] prio=10 latencia=3495 ms
--> [DL-analitica] prio=1 latencia=2139 ms
--> [DL-sesion] prio=10 latencia=2270 ms
--> [DL-portadas] prio=5 latencia=1040 ms
... [DL-portadas] prio=5 cálculo=1459 ms resultado=-282129374951571615
<-- [DL-portadas] prio=5 LISTO (duración=2500 ms)
... [DL-sesion] prio=10 cálculo=4007 ms resultado=-282129374951571615
<-- [DL-sesion] prio=10 LISTO (duración=6281 ms)
... [DL-analitica] prio=1 cálculo=5918 ms resultado=-282129374951571615
<-- [DL-analitica] prio=1 LISTO (duración=8071 ms)
... [DL-catalogo] prio=10 cálculo=6106 ms resultado=-282129374951571615
<-- [DL-catalogo] prio=10 LISTO (duración=9614 ms)
[MAIN] Todos los módulos listos -> LOGIN HABILITADO
Tiempo total real: ≈ 9617 ms / Duración del más lento: 9614 ms / Suma de todas las latencias: 8944 ms
Suma de duraciones reales (latencia + cálculo): 26466 ms
Comparación: total real ≈ más lento (diferencia 3 ms) y no la suma (diferencia 16849 ms)
Orden de finalización: portadas-assets -> sesion-y-perfil -> analiticas -> catalogo-metadatos
```

Con `--cpu` y un procesador lógico (`cpu-afinidad-3`):

```
[MAIN] Procesadores disponibles: 1
[MAIN] Descargando 4 módulos en paralelo...
--> [DL-sesion] prio=10 latencia=3192 ms
--> [DL-catalogo] prio=10 latencia=3264 ms
--> [DL-portadas] prio=5 latencia=3823 ms
--> [DL-analitica] prio=1 latencia=1874 ms
... [DL-catalogo] prio=10 cálculo=3146 ms resultado=-282129374951571615
... [DL-sesion] prio=10 cálculo=3219 ms resultado=-282129374951571615
<-- [DL-sesion] prio=10 LISTO (duración=6414 ms)
<-- [DL-catalogo] prio=10 LISTO (duración=6412 ms)
... [DL-portadas] prio=5 cálculo=3546 ms resultado=-282129374951571615
<-- [DL-portadas] prio=5 LISTO (duración=7531 ms)
... [DL-analitica] prio=1 cálculo=5820 ms resultado=-282129374951571615
<-- [DL-analitica] prio=1 LISTO (duración=7696 ms)
[MAIN] Todos los módulos listos -> LOGIN HABILITADO
Tiempo total real: ≈ 7697 ms / Duración del más lento: 7696 ms / Suma de todas las latencias: 12153 ms
Suma de duraciones reales (latencia + cálculo): 28053 ms
Comparación: total real ≈ más lento (diferencia 1 ms) y no la suma (diferencia 20356 ms)
Orden de finalización: sesion-y-perfil -> catalogo-metadatos -> portadas-assets -> analiticas
```

### 6.2 Tablas de ejecuciones

Valores en ms. Latencias y cálculo, en este orden: catálogo / sesión / portadas / analítica. La salida completa de cada ejecución está en [`docs/ejecuciones/`](docs/ejecuciones/).

**Prueba B · Sin argumentos**

| Ejecución | Latencias que tocaron | Orden de finalización de los módulos | Tiempo total | Más lento | Suma |
|---|---|---|---|---|---|
| `sin-flags-1` | 3588 / 2454 / 2299 / 1526 | analiticas → portadas-assets → sesion-y-perfil → catalogo-metadatos | 3600 | 3598 | 9867 |
| `sin-flags-2` | 3218 / 1352 / 1794 / 2695 | sesion-y-perfil → portadas-assets → analiticas → catalogo-metadatos | 3220 | 3218 | 9059 |
| `sin-flags-3` | 2000 / 1184 / 3496 / 3520 | sesion-y-perfil → catalogo-metadatos → portadas-assets → analiticas | 3567 | 3521 | 10200 |

**Ampliación · Con `--cpu` (12 procesadores lógicos)**

| Ejecución | Latencias | Cálculo | Orden de finalización | Tiempo total | Más lento | Suma de latencias | Suma de duraciones |
|---|---|---|---|---|---|---|---|
| `cpu-1` | 3099 / 3949 / 3467 / 1795 | 6080 / 6052 / 7992 / 3035 | analiticas → catalogo-metadatos → sesion-y-perfil → portadas-assets | 11500 | 11462 | 12310 | 35479 |
| `cpu-2` | 1224 / 1269 / 2708 / 1500 | 1530 / 1499 / 6378 / 1791 | catalogo-metadatos → sesion-y-perfil → analiticas → portadas-assets | 9114 | 9089 | 6701 | 17929 |
| `cpu-3` | 3495 / 2270 / 1040 / 2139 | 6106 / 4007 / 1459 / 5918 | portadas-assets → sesion-y-perfil → analiticas → catalogo-metadatos | 9617 | 9614 | 8944 | 26466 |

**Ampliación · Con `--cpu` limitado a 1 procesador lógico**

| Ejecución | Latencias | Cálculo | Orden de finalización | Tiempo total | Más lento | Suma de latencias | Suma de duraciones |
|---|---|---|---|---|---|---|---|
| `cpu-afinidad-1` | 3848 / 1494 / 3159 / 3627 | 4627 / 1480 / 9103 / 10625 | sesion-y-perfil → catalogo-metadatos → portadas-assets → analiticas | 16932 | 16930 | 12128 | 40649 |
| `cpu-afinidad-2` | 3272 / 2922 / 2878 / 3770 | 8924 / 8916 / 12088 / 10149 | sesion-y-perfil → catalogo-metadatos → portadas-assets → analiticas | 16470 | 16468 | 12842 | 55482 |
| `cpu-afinidad-3` | 3264 / 3192 / 3823 / 1874 | 3146 / 3219 / 3546 / 5820 | sesion-y-perfil → catalogo-metadatos → portadas-assets → analiticas | 7697 | 7696 | 12153 | 28053 |

### 6.3 Análisis del planificador

**Sin argumentos.** En las 3 ejecuciones, el orden de finalización coincide con el orden creciente de latencia. La prioridad no se aprecia: `analiticas` (prioridad 1) termina primera en `sin-flags-1` y última en `sin-flags-3`. Tiene sentido, porque un hilo dormido en `sleep()` no compite por la CPU.

**Con `--cpu` y 12 procesadores.** La latencia sigue pesando, pero ya no decide sola. El orden coincide con el de latencia solo en `cpu-2`. En `cpu-1`, sesión adelanta a portadas, y en `cpu-3`, sesión adelanta a analítica. Las duraciones de cálculo no siguen la prioridad: en `cpu-3`, catálogo (prioridad 10) tarda 6106 ms y portadas (prioridad 5) 1459 ms.

**Con `--cpu` y 1 procesador.** Las 3 ejecuciones terminan en el mismo orden: sesión, catálogo, portadas y analítica, que es el de prioridad descendente. Ese orden no es el de latencia. En `cpu-afinidad-3`, analítica tiene la menor latencia (1874 ms) y termina la última. Entre sesión y catálogo, ambas con prioridad 10, siempre termina antes la de menor latencia.

**Interpretación (hipótesis).** Los datos son compatibles con esta idea: la prioridad de Java es una sugerencia que la JVM traslada al planificador de Windows, y solo se nota cuando varios hilos compiten de verdad por la CPU. Son solo 3 ejecuciones por grupo, así que la conclusión es cautelosa.

**Variabilidad del cálculo (hipótesis sin comprobar).** El valor `cálculo` es tiempo de reloj, no tiempo de CPU. En el grupo `cpu-*`, solo 4 de las 12 fases duran entre 1 y 2 s. La competencia entre estos cuatro hilos no parece explicarlo todo: en `cpu-2`, portadas tarda 6378 ms y, según las duraciones, los otros módulos habían terminado hacia los 3315 ms. El i7-1355U es híbrido, con 2 núcleos de rendimiento y 8 de eficiencia, y de bajo consumo. Según el núcleo que le toque a cada hilo y los límites de potencia, el mismo trabajo podría tardar tiempos distintos. No se ha hecho ninguna prueba que confirme o descarte esta causa.

**¿Acerté en mis predicciones del apartado 2?**

| Predicción | Resultado real | ¿Acerté? |
|---|---|---|
| Con los 4 hilos a la vez, unos 4000 ms (el módulo más lento) | Totales de 3220 a 3932 ms, siempre casi iguales al módulo más lento | Sí |
| Uno detrás de otro, unos 9000 ms | La suma de latencias salió entre 8343 y 10200 ms | Sí, aproximadamente |
| Los hilos terminan siempre en el mismo orden | El orden cambió en cada ejecución | No |
| ¿Terminan antes los de prioridad alta? | Sin argumentos, no. Con `--cpu` y 1 procesador, sí | No lo sabía |
| El total se parece al más lento | Se parece al más lento en todas las ejecuciones | Sí |

Fallé en el orden porque supuse duraciones fijas, y en el programa las latencias son aleatorias, así que el orden cambia. La prioridad solo se notó cuando los hilos compitieron por la CPU, con el cálculo y un solo procesador. Antes no tenía claro que un hilo dormido no usa CPU y que, por eso, la prioridad no cambia nada.

---

## 🧠 7. Preguntas técnicas

### 7.1 Preguntas comunes

**1. ¿Por qué el tiempo total es el del hilo más lento y no la suma de todos?**

`start()` lanza el hilo y devuelve el control enseguida. Por eso, los cuatro hilos están en marcha casi desde el mismo instante. `main` espera en `join()` y solo sigue cuando acaba el último. El programa tarda, por tanto, lo que tarde ese último hilo. En `sin-flags-1`, el total real es 3600 ms, el más lento 3598 ms y la suma de latencias 9867 ms. En las 9 ejecuciones, el total supera al más lento entre 1 y 46 ms.

**2. ¿Qué diferencia real hay entre llamar a `start()` y llamar a `run()`?**

`start()` crea un hilo nuevo, y ese hilo ejecuta `run()`. Llamar a `run()` directamente es una llamada normal a un método: se ejecuta dentro de `main`. Con `run()`, cada descarga empezaría al acabar la anterior y el total se acercaría a la suma de latencias. Además, `Thread.currentThread()` devolvería `main`, y la traza mostraría ese nombre y su prioridad en lugar de `DL-catalogo` y los demás.

**3. ¿Qué pasaría si quitaras los `join()`? ¿Y si pusieras el `join()` justo después de cada `start()`?**

Sin `join()`, `main` seguiría de largo mientras los hilos aún duermen. El tiempo medido sería de casi 0 ms y la cola de finalización estaría incompleta, así que este programa imprimiría `Faltan módulos por descargar -> login NO habilitado`. Los hilos terminarían después, porque no son demonios. Con un `join()` tras cada `start()`, `main` esperaría a cada hilo antes de lanzar el siguiente. Habría hilos, pero no concurrencia entre ellos. El total sería cercano a la suma y el orden sería siempre el del enum.

**4. ¿Qué diferencias hay entre trabajar con duraciones fijas y con latencias aleatorias?**

Con duraciones fijas, los resultados son reproducibles y se pueden comparar directamente entre ejecuciones. También se puede predecir el tiempo total y, en buena medida, quién termina antes. Con latencias aleatorias, cada ejecución es un caso distinto, como `sin-flags-2` y `sin-flags-3`, con totales de 3220 y 3567 ms. No se conoce de antemano el orden ni el total, aunque sí se puede acotar: sin `--cpu`, el total será de al menos el máximo de las latencias, entre 1000 y 4000 ms más un pequeño extra.

**5. El orden en que terminan los hilos puede cambiar entre ejecuciones. ¿A qué se debe? ¿Podrías garantizar un orden concreto?**

Sin argumentos, el orden cambia sobre todo porque cada ejecución sortea latencias distintas. También influye el planificador del sistema, que decide cuándo corre cada hilo, por lo que dos hilos con tiempos muy parecidos podrían intercambiarse. Con `start()` y `join()` no se garantiza un orden de finalización. Para garantizarlo habría que coordinar los hilos: por ejemplo, que cada hilo espere con `join()` al anterior, o usar mecanismos de sincronización. A cambio se perdería parte del paralelismo.

**6. ¿Qué problema costó más depurar y cómo se abordó?**

- **Síntoma:** con `--cpu`, el mismo número de iteraciones daba tiempos muy distintos, de 1459 a 7992 ms en el grupo `cpu-*`.
- **Causa real:** no se ha determinado. La hipótesis del procesador híbrido está sin comprobar.
- **Cómo se investigó:** se comprobó que el resultado era idéntico en todas las fases, se observó la compilación con `-XX:+PrintCompilation` y se midió la carga del equipo. Así se descartaron la JIT y la carga externa.
- **Cómo evitarlo:** medir también tiempo de CPU, no solo tiempo de reloj, y hacer más ejecuciones antes de sacar conclusiones.

**7. Si en lugar de 4 tareas fueran 500, ¿seguiría teniendo sentido crear un `Thread` por tarea? ¿Qué problema crees que aparecería?**

En general, no. Cada hilo de plataforma tiene un coste: su pila de memoria y el tiempo de crearlo y destruirlo. Si muchos hacen cálculo, además, 500 hilos en 12 procesadores pasarían gran parte del tiempo turnándose, y aumentarían los cambios de contexto. El caso del `sleep()` aguanta mejor porque los hilos dormidos no gastan CPU, pero sigue ocupando memoria. Lo habitual sería reutilizar un número limitado de hilos mediante un pool, que en este reto no se usa porque el enunciado se centra en `Thread`.

### 7.2 Prueba B · ArrayList y prioridades

**11. ¿Qué dificultad presenta gestionar el `ArrayList<Thread>`?**

La clave está en separar los bucles. `MainUDITflix` tiene un bucle que hace `start()` en todos los hilos y otro distinto que hace `join()`. Con un único bucle que hiciera ambas cosas, cada hilo esperaría al anterior y se perdería la concurrencia. Además, hay una lista paralela de `TareaDescarga` para leer las latencias y duraciones al final. Añadir un quinto módulo solo exige una línea en el enum `Modulo`, con su nombre, su hilo y su prioridad, porque los hilos se construyen recorriendo `Modulo.values()`.

**12. ¿Qué diferencias se observan al asignar prioridades distintas en el sistema operativo?**

Depende de si hay competencia por la CPU. Sin argumentos, no se aprecia ninguna diferencia: los módulos terminan por orden de latencia. Con `--cpu` y 12 procesadores, el resultado es irregular. Con `--cpu` y 1 procesador, el orden es el de prioridad descendente en las 3 ejecuciones. Es la única situación donde `analiticas` (prioridad 1) termina siempre la última, aun teniendo la menor latencia en `cpu-afinidad-3`.

**13. Si no se ve diferencia (o es pequeña), ¿a qué se debe?**

La causa más directa, sin argumentos, es que los hilos pasan su tiempo en `sleep()`. Un hilo dormido no pide CPU, así que el planificador no tiene nada que repartir. Además, este equipo tiene 12 procesadores lógicos para solo 4 hilos, de modo que incluso calculando pueden correr todos a la vez. Por último, la prioridad es solo una sugerencia que depende del sistema. Los datos con 1 procesador apoyan esta explicación, aunque solo son 3 ejecuciones.

**14. ¿Puede fiarse un programador de las prioridades de Java para garantizar que algo se ejecute antes?**

No. La prioridad es una pista para el planificador y su efecto depende del sistema operativo, de la carga y del número de núcleos. Los datos lo muestran: en `sin-flags-1` termina primero el hilo de prioridad 1, y en `cpu-1` también. Si algo debe ocurrir antes que otra cosa, hay que imponerlo con sincronización explícita, por ejemplo con `join()`, como hace este programa para habilitar el login.

**15. ¿Por qué el programa termina aunque el hilo de «Cargando…» nunca acabe por sí mismo?**

`Spinner` tiene un bucle `while (true)` y nunca termina por sí mismo. Sin embargo, `MainUDITflix` llama a `spinner.setDaemon(true)` antes de `start()`. La JVM termina cuando solo quedan hilos demonio en marcha. Cuando `main` y los hilos `DL-*`, que no son demonios, han acabado, la JVM finaliza y el spinner desaparece con ella. Sin `setDaemon(true)`, el programa no terminaría nunca.

---

## 🌍 8. Mundo real

Una app de streaming típica, al abrirse, necesita varias cosas independientes: los datos del perfil, el catálogo, las imágenes de portada y el envío de métricas. Si las pidiera una detrás de otra, la pantalla inicial tardaría la suma de todas. Pedirlas a la vez la acerca al tiempo de la más lenta.

Este reto se parece en tres puntos: las descargas en paralelo, la espera conjunta antes de continuar (`join()`) y la distinción entre tareas críticas y secundarias.

También hay una diferencia importante. En una app real, las analíticas no deberían bloquear el login: se enviarían en segundo plano. Además, se reutilizaría un número limitado de hilos o se usarían llamadas asíncronas, y se añadirían tiempos máximos y reintentos.

---
## 🪞 9. Autoevaluación

Marca con honestidad, no con optimismo. Nadie te califica esta sección: es para ti y para que el docente sepa dónde ayudarte.

| Puedo explicar a un compañero… | 🔴 No | 🟡 Más o menos | 🟢 Sí |
|---|:-:|:-:|:-:|
| Qué es un `Runnable` y qué es un `Thread` | ☐ | ☐ | X     |
| Por qué `start()` no espera a que el hilo acabe | ☐ | ☐ | X     |
| Qué hace `join()` y dónde lo he puesto | ☐ | X | ☐     |
| Qué estados tiene un hilo y cuándo cambia | ☐ | ☐ | X     |
| Por qué el orden de los hilos no es siempre el mismo | ☐ | X | ☐     |
| Qué son las prioridades y qué garantizan (o no) | ☐ | X | ☐     |
| Qué es un hilo demonio | ☐ | X | ☐     |

**Lo que haría diferente si empezara de nuevo:** Entender mejor la tarea

**Lo que todavía no tengo claro y quiero preguntar en clase:** Que es un hilo demonio

---
## 🤝 10. Declaración de autoría y aprendizaje

Este reto **no permite herramientas de IA generativa**. Consulté únicamente: los apuntes de clase, la documentación oficial de Java y la de IntelliJ IDEA.

- [X] Confirmo que he diseñado, programado y depurado este código aplicando mi propio razonamiento, y que puedo explicarlo línea a línea.
- [X] Confirmo que el vídeo de defensa técnica dura menos de 3 minutos y está grabado con mi propia voz.

---

## 📂 11. Estructura del proyecto

```
reto03_uditflix/
├── src/main/java/es/udit/uditflix/
│   ├── Modulo.java          → definición de los módulos (nombre, hilo, prioridad)
│   ├── TareaDescarga.java   → descarga simulada y fase de cálculo opcional
│   ├── Spinner.java         → hilo demonio «Cargando…»
│   └── MainUDITflix.java    → crea, lanza y espera los hilos; informe final
├── docs/ejecuciones/        → salida de cada ejecución (.txt) y resumen.md
├── pom.xml                  → proyecto Maven, Java 17, sin dependencias
└── README.md                → este documento
```

---
