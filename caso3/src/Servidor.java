import java.util.Random;

/**
 * Servidor de consolidacion y despliegue (thread consumidor final).
 *
 * Responsabilidades:
 *   - Lee eventos uno por uno de su buzon de consolidacion (acotado).
 *   - Por cada evento, simula consolidacion y despliegue durmiendo entre
 *     100 ms y 1000 ms.
 *   - Termina cuando recibe un evento de FIN.
 *
 * Sincronizacion:
 *   - Con los clasificadores: wait/notifyAll en su buzon de consolidacion.
 *   - El sleep durante el procesamiento es un retardo simulado, no es
 *     sincronizacion concurrente.
 */
public class Servidor extends Thread {

    private final int id;
    private final BuzonAcotado buzonConsolidacion;
    private final Random random;

    public Servidor(int id, BuzonAcotado buzonConsolidacion, long semilla) {
        super("Servidor-" + id);
        this.id = id;
        this.buzonConsolidacion = buzonConsolidacion;
        this.random = new Random(semilla);
    }

    @Override
    public void run() {
        int procesados = 0;
        try {
            while (true) {
                Evento e = buzonConsolidacion.retirar(); // espera pasiva
                if (e.esFin()) {
                    System.out.println("[" + getName() + "] Termina: procesados=" + procesados + ".");
                    return;
                }
                long ms = 100 + random.nextInt(901); // [100, 1000]
                Thread.sleep(ms);
                procesados++;
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.err.println("[" + getName() + "] Interrumpido.");
        } catch (Exception ex) {
            System.err.println("[" + getName() + "] Error: " + ex.getMessage());
        }
    }
}
