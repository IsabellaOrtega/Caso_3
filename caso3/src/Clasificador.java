/**
 * Clasificador (thread consumidor-productor).
 *
 * Responsabilidades:
 *   - Lee eventos del buzon de clasificacion.
 *   - Para cada evento identifica su tipo (1..ns) y lo deposita en el buzon
 *     del servidor correspondiente.
 *   - Termina cuando recibe un evento de FIN.
 *   - El ULTIMO clasificador en terminar debe generar y depositar ns eventos
 *     de FIN, uno en el buzon de cada servidor.
 *
 * Sincronizacion:
 *   - Con el administrador y el broker: wait/notifyAll en el buzon de
 *     clasificacion (ambos productores depositan ahi).
 *   - Con los demas clasificadores:
 *       (a) Competencia por los eventos del buzon de clasificacion: se resuelve
 *           con el monitor del buzon + wait() en bucle while.
 *       (b) Deteccion del "ultimo en terminar": por medio del objeto
 *           ContadorClasificadores, cuyo metodo registrarTerminacion() es
 *           synchronized y devuelve true solo al ultimo.
 *   - Con los servidores: wait/notifyAll en cada buzon de consolidacion (acotado).
 */
public class Clasificador extends Thread {

    private final int id;
    private final BuzonAcotado buzonClasificacion;
    private final BuzonAcotado[] buzonesConsolidacion;
    private final ContadorClasificadores contador;

    public Clasificador(int id,
                        BuzonAcotado buzonClasificacion,
                        BuzonAcotado[] buzonesConsolidacion,
                        ContadorClasificadores contador) {
        super("Clasificador-" + id);
        this.id = id;
        this.buzonClasificacion = buzonClasificacion;
        this.buzonesConsolidacion = buzonesConsolidacion;
        this.contador = contador;
    }

    @Override
    public void run() {
        int enrutados = 0;
        try {
            while (true) {
                Evento e = buzonClasificacion.retirar(); // espera pasiva
                if (e.esFin()) {
                    boolean esUltimo = contador.registrarTerminacion();
                    if (esUltimo) {
                        // Ultimo clasificador -> enviar FIN a cada servidor.
                        for (int i = 0; i < buzonesConsolidacion.length; i++) {
                            buzonesConsolidacion[i].depositar(Evento.eventoFin());
                        }
                        System.out.println("[" + getName() + "] Termina (ULTIMO): enrutados=" + enrutados
                                + ". Envio FIN a " + buzonesConsolidacion.length + " servidores.");
                    } else {
                        System.out.println("[" + getName() + "] Termina: enrutados=" + enrutados + ".");
                    }
                    return;
                }
                int tipo = e.getTipo();
                // Validacion defensiva: tipo debe estar en [1, ns]
                if (tipo < 1 || tipo > buzonesConsolidacion.length) {
                    System.err.println("[" + getName() + "] Evento con tipo invalido: " + e);
                    continue;
                }
                buzonesConsolidacion[tipo - 1].depositar(e); // puede bloquearse si esta lleno
                enrutados++;
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.err.println("[" + getName() + "] Interrumpido.");
        } catch (Exception ex) {
            System.err.println("[" + getName() + "] Error: " + ex.getMessage());
        }
    }
}
