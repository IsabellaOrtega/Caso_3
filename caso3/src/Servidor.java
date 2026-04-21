import java.util.Random;


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
