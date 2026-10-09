# Reto 2 · Pipeline de Auditoría UDITversum (Fase 1)

**Módulo:** 0490 · Programación de Servicios y Procesos
**Autor/a:** *Daniel Baeza*
**Tecnología:** Java + `ProcessBuilder` (procesos del sistema operativo)
**RA vinculado:** RA1 · Programación de aplicaciones compuestas por varios procesos

> 💡 **Cómo usar este README:** no es un trámite que se rellena al final. Es tu **cuaderno de pensamiento** durante el reto. Las secciones marcadas con 🧠 sirven para que pienses sobre *cómo* estás pensando. Si las rellenas de golpe en el último minuto pierden todo su valor (y se nota en la defensa oral).
>
> 🗣️ **Importante:** durante la defensa te pediré modificar tu código en directo. Todo lo que escribas aquí debe ser algo que puedas explicar sin mirar la pantalla.

---

## 📺 Qué es esta app

Un programa de consola que simula la **primera fase de una auditoría de UDITversum**. El programa:

1. Lanza **dos comprobaciones (`ping`) a la vez**, cada una en su propio proceso del sistema operativo.
2. **Espera** a que ambas terminen.
3. Lee el **código de salida** de cada una.
4. Según el resultado combinado, **toma una decisión** y abre una aplicación del sistema (Bloc de Notas o Calculadora).

```
        ┌──────────────┐
        │  Mi programa │
        │   (Java)     │
        └──────┬───────┘
               │ start()          start()
        ┌──────┴──────┐    ┌──────┴──────┐
        ▼                         ▼
  ┌───────────┐             ┌───────────┐
  │  ping A   │             │  ping B   │   ← corren a la vez
  └─────┬─────┘             └─────┬─────┘
        │ waitFor()               │ waitFor()
        └───────────┬─────────────┘
                    ▼
          ¿códigos de salida?
                    │
        ┌───────────┴───────────┐
        ▼                       ▼
   Bloc de Notas           Calculadora
```

*(Ajusta el esquema y los nombres a lo que hace realmente tu programa.)*

📸 **<img width="1800" height="667" alt="image" src="https://github.com/user-attachments/assets/ae61c62d-dd6e-40fb-8e0b-a83b828c2f9f" />
**

---

## 🧠 Antes de empezar: planifico *(5 min, sin tocar el teclado)*

Responde **antes** de escribir una sola línea de código. No importa si te equivocas: lo importante es dejar escrito qué pensabas.

**Con mis palabras, ¿qué me pide el reto?** *(sin copiar el enunciado)*
*(El reto me pide lanzar 2 procesos en paralelo y esperar los códigos de salida. Si ambos devuelven 0 se tiene que abrir el Bloc de Notas y si alguno de los 2 procesos falla deberá abrirse la calculadora.)*

**¿Qué parte del Reto 1 voy a reutilizar tal cual?**
*Reutilizaré tal cual como se crean los Procesos.*

**¿Qué es nuevo respecto al Reto 1 y me da más respeto?**
*Respecto al Reto 1 lo nuevo que hemos implementado es el uso de el if y el else para decidir si se abre la calculadora o el Bloc de Notas.*

**Mi plan en 4-5 pasos, en orden:**
1. *Crear los Procesos*
2. *Comienzo el proceso con .start()*
3. *Esperar con el waitFor() al segundo proceso para que los 2 sean paralelos*
4. *Comparo los procesos.*
5. *Mostrar por pantalla el resultado de los procesos y abrir la respectiva app, calculadora o bloc de notas.*

**Predicciones** *(comprueba al final si acertaste)*

| Escenario | ¿Qué código de salida espero en cada ping? | ¿Qué aplicación se abre? |
|---|---|---|
| Los dos pings a `127.0.0.1` | *0 y 0* | *Bloc de Notas* |
| Un ping válido y otro a una dirección inexistente | *0 y 1* | *Calculadora* |
| Los dos pings a direcciones inexistentes | *1 y 1* | *Calculadora* |

**Predicción de tiempo:** si cada ping tarda unos 3 segundos, ¿cuánto tardará mi programa en total si los lanzo en paralelo? ¿Y si los lanzara uno detrás de otro?
*En paralelo tardarían 3 segundos y uno detrás de otro tardaría 6 segundos*

---

## 🎯 Objetivo del reto

Dar el salto de **gestionar un proceso tras otro** (Reto 1) a **coordinar varios procesos simultáneos**, aplicando:

- Creación de procesos con `ProcessBuilder` y `start()`.
- **Ejecución concurrente:** lanzar ambos procesos antes de esperar a ninguno.
- Sincronización con `waitFor()` y lectura del **código de salida**.
- **Lógica condicional** (`if` con `&&` / `||`) para decidir en función de varios resultados.
- Gestión de errores con `try/catch` (`IOException`, `InterruptedException`).

---

## 🛠️ Componentes y conceptos utilizados

> Rellena la columna **"con mis palabras"** *sin mirar tus apuntes*. Después compara con ellos y corrige en otro color o con un comentario. Esa diferencia es exactamente lo que aún no tienes del todo claro.

| Componente / concepto | Para qué se usa en esta app | Con mis palabras |
|---|---|---|
| `ProcessBuilder` | Prepara la orden que se enviará al sistema operativo (ping, notepad, calc) | Prepara los procesos | 
| `start()` | Lanza de verdad el proceso y **devuelve el control enseguida**, sin esperar | Comienza los procesos | 
| `Process` | Objeto con el que controlo cada proceso ya en marcha | Controla lo que está en marcha | 
| `waitFor()` | Bloquea mi programa hasta que ese proceso termina y devuelve su código de salida | Espera a los procesos para devolver el código de salida |  
| Código de salida (`int`) | Dice cómo terminó el proceso: `0` = éxito, distinto de `0` = fallo | Código que dice si el proceso termina de manera exitosa o no |  
| `&&` (AND) | Se cumple solo si **las dos** condiciones son verdaderas | Condición lógica que se cumple cuando las dos son true | 
| `\|\|` (OR) | Se cumple si **al menos una** condición es verdadera | Condición lógica que se cumple cuando una de las dos es true | 
| `try/catch` | Captura errores que Java no puede evitar (el SO no encuentra el programa, etc.) | Comprueba los errores |  
| `InterruptedException` | Excepción que obliga a gestionar `waitFor()` por si el hilo es interrumpido | Excepción que obliga a gestionar waitFor() | 

**¿Cómo se llaman mis dos objetos `Process` y qué lanza cada uno?**
*(Se llaman p1 y p2 y lanzan o una calculadora o un bloc de notas.)*

---

## 🔀 Secuencial vs paralelo: el corazón de este reto

Lo que separa un buen Reto 2 de uno que "funciona pero no cumple" es **dónde pones los `waitFor()`**. Compara las dos líneas de tiempo (suponiendo que cada ping tarda 3 s):

**❌ Secuencial** (`waitFor()` antes del segundo `start()`):

```
t=0s   start(ping A) ──────────── waitFor() ── termina en t=3s
t=3s                              start(ping B) ──────────── waitFor() ── termina en t=6s
                                                                          TOTAL ≈ 6 s
```

**✅ Paralelo** (los dos `start()` primero, los `waitFor()` después):

```
t=0s   start(ping A) ─┐
t=0s   start(ping B) ─┤  los dos procesos corren a la vez
t=3s   waitFor(A) ✔  waitFor(B) ✔
                                  TOTAL ≈ 3 s
```

**Mi orden real de llamadas, copiado de mi código** *(pega solo las líneas relevantes)*:

```java
// (pega aquí tus llamadas a start() y waitFor() en el orden en que aparecen)
```

---

## 🔢 Tabla de verdad de mi decisión

Completa con **tu** lógica real (la del enunciado):

| Código ping A | Código ping B | ¿Ping A OK? | ¿Ping B OK? | Condición (`&&` / `\|\|`) | Aplicación que abro |
|---|---|---|---|---|---|
| 0 | 0 | Sí | Sí | && | Bloc de Notas |
| 0 | ≠ 0 | Sí | No | \|\| | Calculadora|
| ≠ 0 | 0 | No | Sí | \|\| | Calculadora |
| ≠ 0 | ≠ 0 | No | No | && | Calculadora |

**¿Cambiaría el resultado de alguna fila si cambiara `&&` por `||`? ¿En cuáles?**
*(Sí, con el && la 2 y la 3 cambian)*

---

## 🚀 Cómo ejecutar el proyecto

1. Clonar o abrir el proyecto en IntelliJ IDEA.
2. Esperar a que indexe el proyecto.
3. Ejecutar (▶) la clase principal.

⚠️ **Dependencia del sistema operativo:** `ping` usa `-n` en Windows y `-c` en Linux/Mac, y `notepad.exe` / `calc.exe` solo existen en Windows. Indica con qué sistema lo has probado: *(Con Windows)*

---

## 🔍 Mientras programo: mi diario de decisiones

Cada vez que te atasques, cambies de idea o algo falle, anota una entrada. Tres líneas bastan. No se trata de quedar bien: se trata de que dentro de un mes puedas reconstruir **cómo pensaste**.

| Qué intentaba | Qué pasó realmente | Qué hice / qué aprendí |
|---|---|---|
| *(ejemplo)* Que los dos pings corrieran a la vez | El programa tardaba el doble de lo esperado | Me di cuenta de que había puesto un `waitFor()` entre los dos `start()` y... |
| Que se abriera la app correcta | Se abría la Calculadora aunque los dos pings iban bien | Había puesto || en lugar de &&. Corregí la condición. |


**Mi pregunta-brújula cuando me bloqueo:**
1. ¿Qué espero que haga esta línea? → Que `start()` lance el ping y me devuelva el control enseguida.
2. ¿Qué está haciendo realmente? *(imprimo valores para comprobarlo)* → Imprimí `System.currentTimeMillis()` antes de cada `start()` y el segundo arrancaba 3 s más tarde.
3. ¿En qué punto exacto se separan las dos respuestas? → En el `waitFor()` que había colocado entre los dos `start()`.

---

## 🧠 Análisis técnico (preparación para la defensa)

### 1. Secuencial vs paralelo

Los dos `start()` consecutivos, antes de cualquier `waitFor()`, garantizan que los pings corren a la vez. `start()` le pide al sistema operativo que cree el proceso y devuelve el control a mi programa sin esperar. El ping lo ejecuta el SO, no mi programa Java. Si pusiera el primer `waitFor()` antes del segundo `start()`, mi programa se quedaría bloqueado hasta que acabara el primer ping. Solo existiría un proceso vivo a la vez y el programa tardaría ≈ 6 s en lugar de ≈ 3 s.

### 2. El código de salida (exit code)

`waitFor()` devuelve un `int`. Un `0` significa que el proceso terminó correctamente y cualquier valor distinto de `0` indica que terminó con algún tipo de error. Solo hay una forma de acabar bien, pero muchas de acabar mal, así que el estándar reserva el 0 para el éxito y deja los demás números para distinguir la causa del fallo. No es lo mismo que el ping falle (el programa se ejecutó y devolvió un código ≠ 0) que el ping no pueda ejecutarse (Java no logra arrancarlo y lanza `IOException`).

### 3. Lógica condicional

```java
if (codigo1 == 0 && codigo2 == 0) {
    new ProcessBuilder("notepad.exe").start();
} else {
    new ProcessBuilder("calc.exe").start();
}
```

Uso `&&` porque el Bloc de Notas solo debe abrirse cuando **ambas** comprobaciones tienen éxito. Si uno falla, el resultado de la auditoría no es bueno y se abre la Calculadora. Con `||` cambiarían las filas 2 y 3 de mi tabla de verdad (0/≠0 y ≠0/0): se abriría el Bloc de Notas con que *uno* funcionara, y eso contradice el enunciado.

### 4. Gestión de excepciones

Si escribo mal el nombre del ejecutable (`notepd.exe`) o ejecuto el programa en Linux, donde `notepad.exe` no existe, Java no consigue ni arrancar el proceso y entra en el `catch` de `IOException`. Un ping a una dirección inexistente no entra en el `catch`: el proceso arranca bien y termina con código ≠ 0.

---

## 🛡️ Preparación para la defensa

- ☑ Cambiar la condición para que se abra la Calculadora **solo si falla uno de los dos pings**.
- ☑ Añadir un **tercer ping** en paralelo y que la decisión dependa de los tres.
- ☑ Mostrar el **PID** de cada proceso al lanzarlo.
- ☑ Medir y mostrar **cuántos milisegundos** tarda en total el programa.
- ☑ Hacer que el programa funcione en **Linux**.
- ☑ Provocar a propósito una `IOException` y mostrar un mensaje claro al usuario.
- ☑ Explicar qué pasaría si quito el `waitFor()`.

**¿Cuál me costó más y por qué?**
*El segundo ping con la condición combinada. Tuve que rehacer la tabla de verdad con 8 filas y asegurarme de que el `&&` con tres códigos cubría todos los casos.*

---

## 🧭 Del Reto 1 al Reto 2: cómo di el salto

**¿Qué hacía mi Reto 1 que aquí ya no me sirve tal cual?**
*Lanzaba y esperaba cada proceso dentro del mismo bucle. Cada vuelta se bloqueaba hasta que terminaba el proceso, así que nunca había dos a la vez.*

**¿Qué he tenido que cambiar para que dos procesos corran simultáneamente?**
*Separar el lanzamiento de la espera: primero todos los `start()` y después todos los `waitFor()`.*

**¿Qué ventaja tiene lanzar en paralelo? ¿Y qué problema nuevo aparece?**
*El tiempo total es el del proceso más lento, no la suma de todos. El problema nuevo es que ahora dependo de varios resultados y tengo que combinarlos en una sola decisión con `&&` / `||`.*

**Si mañana el pipeline tuviera 50 comprobaciones en lugar de 2, ¿seguiría teniendo sentido mi estructura?**
*No. Copiar 50 `start()` y 50 `waitFor()` no escala. Guardaría los procesos en una `List<Process>`, con un bucle para lanzarlos todos y otro para esperarlos y acumular si todos devolvieron 0.*

---

## 🧠 Qué he aprendido

- **`start()` vs `waitFor()`:** lanzar un proceso y esperarle son cosas distintas porque `start()` solo pide al SO que lo cree y sigue, mientras que `waitFor()` bloquea mi programa hasta que acaba.
- **Paralelismo real:** dos procesos corren "a la vez" porque los ejecuta el sistema operativo de forma independiente, y yo los lanzo los dos antes de esperar a ninguno.
- **Código de salida:** que sea `0` significa que el proceso terminó bien, y que sea distinto de `0` significa que terminó con error.
- **`&&` vs `||`:** elegí `&&` porque el éxito de la auditoría exige que las dos comprobaciones funcionen.
- **`IOException` vs ping fallido:** la diferencia es que la primera es no poder arrancar el proceso, y el segundo es que arrancó y terminó con código ≠ 0.
- **Fiabilidad de mi decisión:** lo que decide mi programa se basa solo en el código de salida del ping. Puede equivocarse si el ping falla por un firewall que bloquea ICMP o por un fallo de DNS, aunque el servicio funcione.

---

## 🐞 Dificultades y cómo las resolví

**Dificultad 1:**
- Qué síntoma vi: el programa tardaba ~6 s en vez de ~3 s.
- Cuál era la causa real: había un `waitFor()` entre los dos `start()`, así que el segundo ping no arrancaba hasta que acababa el primero.
- Cómo la encontré: depuración, imprimiendo marcas de tiempo con `System.currentTimeMillis()` antes y después de cada llamada.
- Cómo evitaré que me vuelva a pasar: escribir primero todos los `start()` y después todos los `waitFor()`.

**Dificultad 2:**
- Qué síntoma vi: con una IP inexistente no entraba en el `catch`.
- Cuál era la causa real: un ping fallido no es una `IOException`; el proceso arranca bien y devuelve un código ≠ 0.
- Cómo la encontré: apuntes de clase y documentación oficial de `ProcessBuilder`.
- Cómo evitaré que me vuelva a pasar: comprobar siempre el código de salida, no esperar una excepción.

---

## 🪞 Autoevaluación

| Puedo explicar a un compañero… | 🔴 No | 🟡 Más o menos | 🟢 Sí |
|---|:---:|:---:|:---:|
| Qué hace `ProcessBuilder` | ☐ | ☐ | ☑ |
| Por qué `start()` no espera | ☐ | ☐ | ☑ |
| Qué línea hace que mis procesos sean paralelos | ☐ | ☐ | ☑ |
| Qué pasaría si moviera el `waitFor()` | ☐ | ☐ | ☑ |
| Qué devuelve `waitFor()` y qué significa `0` | ☐ | ☐ | ☑ |
| Por qué uso `&&` / `\|\|` en mi condición | ☐ | ☑ | ☐ |
| Cuándo se entra en el `catch` de `IOException` | ☐ | ☐ | ☑ |

**Mis predicciones del principio, ¿acerté?**
*Sí en los tres escenarios (0/0 → Bloc de Notas; 0/≠0 y ≠0/≠0 → Calculadora) y en el tiempo (≈ 3 s en paralelo, ≈ 6 s en secuencial). Me equivoqué en el plan: dije que el `waitFor()` hacía el paralelismo, pero lo hace lanzar los dos `start()` antes de esperar.*

**Lo que haría diferente si empezara de nuevo:**
*Escribiría primero la tabla de verdad y después el `if`, y mediría el tiempo desde el principio para comprobar el paralelismo.*

**Lo que todavía no tengo claro y quiero preguntar en clase:**
*Qué ocurre si un proceso se queda colgado y nunca termina, y cómo poner un tiempo máximo de espera (`waitFor` con timeout).*

---

## 🤝 Declaración de autoría y aprendizaje

Este reto **no permite herramientas de IA generativa**. Consulté únicamente: los apuntes de clase, la documentación oficial de Java e IntelliJ IDEA.

☐ Confirmo que he diseñado, programado y depurado este código aplicando mi propio razonamiento, y que puedo explicarlo línea a línea.

☐ Entiendo que durante la defensa el profesor me pedirá realizar pequeñas modificaciones sobre este código para comprobar mi comprensión del multiproceso.

---

## 🔗 Enlace

GitHub: *(tu repositorio)*
