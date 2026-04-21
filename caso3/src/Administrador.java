import java.util.Random;

/**
 * Sincronizacion:
 *   - Con broker: wait/notifyAll en el buzon de alertas (espera pasiva).
 *   - Con clasificadores: wait/notifyAll en el buzon de clasificacion (acotado).
 */
public class Administrador extends Thread {

    private final int nc;
    private final BuzonIlimitado buzonAlertas;
    private final BuzonAcotado buzonClasificacion;
    private final Random random;

    public Administrador(int nc,
                         BuzonIlimitado buzonAlertas,
                         BuzonAcotado buzonClasificacion,
                         long semilla) {
        super("Administrador");
        this.nc = nc;
        this.buzonAlertas = buzonAlertas;
        this.buzonClasificacion = buzonClasificacion;
        this.random = new Random(semilla);
    }

    @Override
    public void run() {
        int total = 0;

        try {
            while (true) {
                Evento e = buzonAlertas.retirar();

                if (e.esFin()) {
                    // mandar fin a clasificadores
                    for (int i = 0; i < nc; i++) {
                        buzonClasificacion.depositar(Evento.eventoFin());
                    }
                    System.out.println("Administrador termino");
                    return;
                }

                total++;

                int n = random.nextInt(21);
                if (n % 4 == 0) {
                    buzonClasificacion.depositar(e);
                }
                // si no, simplemente lo ignora

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
