package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PildoraMonitor {

    public static void main(String[] args) {
        System.out.println("===MONITOR UDITFLIX===");
        System.out.println("Comprobando servicio...");

        // TRY CATCH
        // Lanzar un programa externo o esperar a que termine PUEDE FALLAR
        // try -> "Intenta hacer esto"
        // catch -> "Si algo sale mal, haz esto otro en vez de romper el programa

        try {

            // PASO 1: PREPARAR el proceso (todavia no se ejecuta)
            // ProcessBuilder es el "encargado" que prepara la orden que le daremos al sistema operativo
            // es como rellenar un formulario
            // "pin" -> es el programa que queremos ejecutar
            // "-n" -> opcion de Windows: numero de intentos
            // "1" -> haz solo 1 intento (asi termina mas rapido)
            // "127.0.0.1 -> a quien hacemos ping : nuestro propio ordenador
            //                  (siempre responde, simula un servicio ACTIVO)
            // ojo "-n" solo vale en Windows. En linux y mac seria "-c"
            ProcessBuilder pb = new ProcessBuilder(
              "ping", "-n", "1", "127.0.0.1"
            );

            // PASO 2: UNIR los dos canales de salida
            // Todo programa tiene dos canales por los que "habla"
            // - salida normal (lo que funciona bien)
            // - salida de error (los mensajes de fallo)
            // Con redirectErrorStream(ture) lo juntamos en UNO SOLO
            // Así leyendo un unico canal, vemos TODO lo que el proceso diga
            // sea un resultado normal o un error
            pb.redirectErrorStream(true);

            //PASO 3: LANZAR el proceso
            // start() es el botón de "enviar". Ahora si el sistema operativo crea un programa nuevo (ping)
            // que corre por su cuenta con su propia memoria separado de nuestro programa java.
            // Process es el objeto con el que controlamos ese programa

            Process proceso = pb.start();

            //PASO 4: MOSTRAR EL PID
            // PID = Process IDentifier. Es el "DNI" del proceso

            System.out.println("PID: " + proceso.pid());

            //PASO 5: PREPARAR la lectura de lo que dice el proceso

            //El proceso ping escribe su propia consola, que Java no lo ve
            //Para escucharlo nos "conectamos" a su salida con una cadena:
            // proceso.getInputStream() -> la "tuberia" por la que sale el texto del proceso ( en bytes).
            // new InputStreamReader(...) -> traduce esos bytes a letras.
            // new BufferedReader(...) -> nos deja ller linea a linea

            BufferedReader lector = new BufferedReader(
                    new InputStreamReader(proceso.getInputStream())
            );
            // Variable donde guardaremos cada linea que vayamos leyendo
            // Todavia esta vacia
            String linea;

            //PASO 6:  leer todo el proceso va escribiendo
            while((linea= lector.readLine()) != null) {
                System.out.println(linea);
            }
            // PASO 7: ESPERAR a que el proceso termine
            int codigo = proceso.waitFor();

            //PASO 8: INTERPRETAR EL resultado
            if (codigo ==0) {
                System.out.println("ESTADO: SERVICIO ACTIVO");
            } else {
                System.out.println("ESTADO: SERVICIO CON ERROR");
            }

        }catch (IOException e) {
            System.out.println("No se pudo lanzar el proceso");
        } catch (InterruptedException e) {
            System.out.println("La ejecucion fue interrumpida");
        }
    }


}
