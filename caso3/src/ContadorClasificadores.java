
public class ContadorClasificadores {

    private final int total;
    private int terminados;

    public ContadorClasificadores(int total) {
        this.total = total;
        this.terminados = 0;
    }

    /**
     * Registra que un clasificador ha terminado.
     * @return true si este es el ultimo
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
