import java.util.LinkedList;
import java.util.Queue;

public class Buzon {
    private Queue<Evento> cola = new LinkedList<>();

    public synchronized void depositar(Evento e) {
        cola.add(e);
        notifyAll();
    }

    public synchronized Evento quitar() throws InterruptedException {
        while (cola.isEmpty()) {
            wait();
        }
        return cola.poll();
    }
}
