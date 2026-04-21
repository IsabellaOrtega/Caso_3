========================================================================
Caso 3 - 20261 - Concurrencia y Sincronizacion de Procesos
Simulador Concurrente de Sistema IoT para Campus Universitario
Autor: Alejandro Parada (aparada)
========================================================================

CONTENIDO
---------
  src/                      Codigo fuente (.java)
    Evento.java
    BuzonIlimitado.java
    BuzonAcotado.java
    ContadorClasificadores.java
    Sensor.java
    Broker.java
    Administrador.java
    Clasificador.java
    Servidor.java
    Config.java
    Main.java
  config.txt                Archivo de configuracion de ejemplo
  informe.docx              Informe de diseno, sincronizacion y validacion
  README.txt                Este archivo

REQUISITOS
----------
  JDK 8 o superior (se uso unicamente la API estandar de Java).

COMPILAR
--------
  $ cd caso3
  $ javac -d out src/*.java

EJECUTAR
--------
  $ java -cp out Main config.txt
  (si no se pasa argumento, usa "config.txt" por defecto)

FORMATO DEL ARCHIVO DE CONFIGURACION
------------------------------------
Un parametro por linea, con formato clave=valor. Las lineas vacias y las
que empiezan con '#' son ignoradas.

  ni    = numero de sensores
  base  = numero base de eventos (sensor i genera base*i eventos)
  nc    = numero de clasificadores
  ns    = numero de servidores de consolidacion
  tam1  = capacidad del buzon de clasificacion
  tam2  = capacidad de cada buzon de consolidacion

SALIDA DEL PROGRAMA
-------------------
Al finalizar, el programa imprime:
  - Una linea por cada thread que termina, con sus estadisticas.
  - Una validacion final que verifica que todos los buzones quedan
    vacios y reporta el tiempo total de ejecucion.

Si la validacion falla (alguno de los buzones queda con eventos), el
programa termina con codigo de salida distinto de cero.

OBSERVACION SOBRE LAS PRIMITIVAS UTILIZADAS
-------------------------------------------
De acuerdo con el enunciado, la sincronizacion se implementa
exclusivamente con: synchronized, wait, notify, notifyAll, yield,
join y CyclicBarrier.  No se utilizan Semaphore, Lock, AtomicInteger,
BlockingQueue ni ninguna otra abstraccion de java.util.concurrent
(excepto CyclicBarrier, que esta explicitamente permitido).

Ver informe.docx para el detalle de diseno, el diagrama de clases, la
explicacion de la sincronizacion entre cada pareja de objetos, y los
resultados de la validacion.
