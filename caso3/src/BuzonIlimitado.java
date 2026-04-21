import java.util.LinkedList;
import java.util.Queue;

/**
 * Buzon (mailbox) de capacidad ilimitada.
 *
 * Se usa en los puntos del sistema donde el productor NO puede bloquearse
 * porque se necesita aceptar cualquier evento recibido:
 *   - Buzon de entrada de eventos (de los sensores al broker).
 *   - Buzon de alertas (del broker al administrador).
 *
 * Sincronizacion:
 *   - ESPERA PASIVA con wait()/notifyAll() sobre el monitor del propio buzon.
 *   - Los depositantes nunca se bloquean (capacidad ilimitada).
 *   - Los consumidores se bloquean pasivamente si el buzon esta vacio.
 *
 * Invariantes:
 *   - Todas las operaciones se realizan bajo el mismo monitor (this) para
 *     garantizar exclusion mutua sobre la cola interna.
 *   - Se usa notifyAll() para no perder notificaciones aunque solo haya
 *     un consumidor esperando; es seguro y simple.
 */
public class BuzonIlimitado {

    private final Queue<Evento> cola = new LinkedList<>();
    private final String nombre;

    public BuzonIlimitado(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Deposita un evento en el buzon. Nunca se bloquea (capacidad ilimitada).
     * Despierta a posibles consumidores en espera.
     */
    public synchronized void depositar(Evento evento) {
        cola.add(evento);
        notifyAll();
    }

    /**
     * Retira un evento del buzon. Si el buzon esta vacio, el hilo espera
     * pasivamente (wait()) hasta que haya un evento disponible.
     *
     * El patron while+wait() maneja correctamente las "wake-ups espureas"
     * y el caso de multiples consumidores compitiendo por el mismo evento.
     */
    public synchronized Evento retirar() throws InterruptedException {
        while (cola.isEmpty()) {
            wait(); // espera pasiva
        }
        return cola.poll();
    }

    /** Devuelve la cantidad actual de eventos en el buzon (para validacion). */
    public synchronized int tamano() {
        return cola.size();
    }

    public String getNombre() {
        return nombre;
    }
}
