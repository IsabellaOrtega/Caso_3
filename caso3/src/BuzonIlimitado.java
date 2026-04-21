import java.util.LinkedList;
import java.util.Queue;

public class BuzonIlimitado {

    private final Queue<Evento> cola = new LinkedList<>();
    private final String nombre;

    public BuzonIlimitado(String nombre) {
        this.nombre = nombre;
    }

    public synchronized void depositar(Evento evento) {
        cola.add(evento);
        notifyAll();
    }

    public synchronized Evento retirar() throws InterruptedException {
        while (cola.isEmpty()) {
            wait(); // espera pasiva
        }
        return cola.poll();
    }


    public synchronized int tamano() {
        return cola.size();
    }

    public String getNombre() {
        return nombre;
    }
}
