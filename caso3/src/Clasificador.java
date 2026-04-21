
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
                Evento e = buzonClasificacion.retirar(); 
                if (e.esFin()) {
                    boolean esUltimo = contador.registrarTerminacion();
                    if (esUltimo) {
                        // Ultimo clasificador
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
                if (tipo < 1 || tipo > buzonesConsolidacion.length) {
                    System.err.println("[" + getName() + "] Evento con tipo invalido: " + e);
                    continue;
                }
                buzonesConsolidacion[tipo - 1].depositar(e); 
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
