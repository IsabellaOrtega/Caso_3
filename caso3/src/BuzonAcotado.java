/**
 * Buzon (mailbox) de capacidad acotada, implementado como un buffer circular.
 *
 * Se usa en los puntos del sistema donde se debe limitar el numero de eventos
 * pendientes (control de flujo):
 *   - Buzon para clasificacion (capacidad tam1): broker/admin -> clasificadores.
 *   - Buzon de cada servidor de consolidacion (capacidad tam2): clasificadores -> servidor.
 *
 * Sincronizacion:
 *   - ESPERA PASIVA con wait()/notifyAll() sobre el monitor del propio buzon.
 *   - Los productores se bloquean pasivamente si el buzon esta lleno.
 *   - Los consumidores se bloquean pasivamente si el buzon esta vacio.
 *   - Se usa notifyAll() para despertar a todos los hilos posiblemente
 *     bloqueados (productores y consumidores comparten el mismo monitor).
 *
 * Este es el clasico "productor-consumidor con buffer acotado", solucionado
 * exclusivamente con las primitivas basicas de Java (synchronized, wait,
 * notifyAll) como exige el enunciado.
 */
public class BuzonAcotado {

    private final Evento[] buffer;
    private final int capacidad;
    private int in;       // siguiente posicion donde depositar
    private int out;      // siguiente posicion donde retirar
    private int cantidad; // numero de elementos actualmente en el buzon
    private final String nombre;

    public BuzonAcotado(String nombre, int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser positiva: " + capacidad);
        }
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.buffer = new Evento[capacidad];
        this.in = 0;
        this.out = 0;
        this.cantidad = 0;
    }

    
    public synchronized void depositar(Evento evento) throws InterruptedException {
        while (cantidad == capacidad) {
            wait(); // espera pasiva: buzon lleno
        }
        buffer[in] = evento;
        in = (in + 1) % capacidad;
        cantidad++;
        notifyAll(); // despertamos a posibles consumidores
    }

    public synchronized Evento retirar() throws InterruptedException {
        while (cantidad == 0) {
            wait(); // espera pasiva: buzon vacio
        }
        Evento e = buffer[out];
        buffer[out] = null; // permitir al GC liberar la referencia
        out = (out + 1) % capacidad;
        cantidad--;
        notifyAll(); // despertamos a posibles productores
        return e;
    }

    /** Devuelve la cantidad actual de eventos (para validacion). */
    public synchronized int tamano() {
        return cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }
}
