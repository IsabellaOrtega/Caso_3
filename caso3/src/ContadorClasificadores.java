/**
 * Contador concurrente que registra cuantos clasificadores han terminado y
 * permite identificar al ULTIMO clasificador en terminar.
 *
 * El ultimo clasificador es el responsable de generar y depositar los eventos
 * de fin para los servidores de consolidacion (ns eventos de fin, uno por
 * cada servidor).
 *
 * Sincronizacion:
 *   - Toda la operacion es atomica gracias a synchronized.
 *   - No se requiere wait/notify, pues el contador no bloquea: el ultimo
 *     clasificador simplemente recibe true como respuesta.
 */
public class ContadorClasificadores {

    private final int total;
    private int terminados;

    public ContadorClasificadores(int total) {
        this.total = total;
        this.terminados = 0;
    }

    /**
     * Registra que un clasificador ha terminado.
     * @return true si este es el ULTIMO clasificador en terminar.
     */
    public synchronized boolean registrarTerminacion() {
        terminados++;
        return terminados == total;
    }

    public synchronized int getTerminados() {
        return terminados;
    }

    public int getTotal() {
        return total;
    }
}
