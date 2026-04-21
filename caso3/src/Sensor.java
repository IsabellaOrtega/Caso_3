import java.util.Random;
import java.util.concurrent.CyclicBarrier;

/**
 * Sensor IoT (thread productor).
 *
 * Responsabilidades:
 *   - Genera un numero fijo de eventos (base * id).
 *   - Cada evento tiene un identificador unico s<id>-<secuencial>.
 *   - Cada evento lleva un tipo aleatorio entre 1 y ns (tipo = servidor destino).
 *   - Deposita los eventos en el buzon de entrada (capacidad ilimitada).
 *   - Termina cuando ha generado todos sus eventos asignados.
 *
 * Sincronizacion:
 *   - Con el buzon de entrada: uso del metodo synchronized depositar().
 *   - Con los demas sensores: CyclicBarrier para iniciar todos a la vez.
 *   - Con el broker: via wait/notifyAll del buzon de entrada (espera pasiva).
 *
 * Patron de espera adicional (ESPERA SEMI-ACTIVA):
 *   - Entre deposito y deposito, el sensor llama a Thread.yield() para
 *     ceder voluntariamente la CPU y reducir la contencion sobre el
 *     monitor del buzon de entrada. Esto ilustra el uso de espera
 *     semi-activa exigido por el enunciado.
 */
public class Sensor extends Thread {

    private final int id;                      // Identificador del sensor (1..ni)
    private final int numEventos;              // Numero de eventos a generar
    private final int ns;                      // Numero de servidores (rango de tipo)
    private final BuzonIlimitado buzonEntrada; // Buzon destino
    private final Random random;               // Aleatorio para tipo de evento
    private final CyclicBarrier barreraInicio; // Para iniciar sincronizados

    public Sensor(int id, int numEventos, int ns,
                  BuzonIlimitado buzonEntrada,
                  CyclicBarrier barreraInicio,
                  long semilla) {
        this.id = id;
        this.numEventos = numEventos;
        this.ns = ns;
        this.buzonEntrada = buzonEntrada;
        this.barreraInicio = barreraInicio;
        this.random = new Random(semilla);
    }

    @Override
    public void run() {
        try {
            // Sincronizacion de arranque: todos los sensores empiezan a la vez.
            barreraInicio.await();

            for (int secuencial = 1; secuencial <= numEventos; secuencial++) {
                int tipo = random.nextInt(ns) + 1; // [1, ns]
                Evento e = new Evento(id, secuencial, tipo);
                buzonEntrada.depositar(e);

                // ESPERA SEMI-ACTIVA: cedemos la CPU para que el broker y los
                // demas sensores tengan oportunidad de avanzar. A diferencia
                // de una espera pasiva (wait), el thread sigue listo.
                Thread.yield();
            }
            System.out.println("[" + getName() + "] Termina: genero " + numEventos + " eventos.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
