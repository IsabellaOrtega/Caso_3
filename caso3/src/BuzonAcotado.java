
public class BuzonAcotado {

    private final Evento[] buffer;
    private final int capacidad;
    private int in;       // siguiente posicion donde depositar
    private int out;      // siguiente posicion donde retirar
    private int cantidad; // numero de elementos en el buzon
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
            wait();
        }
        buffer[in] = evento;
        in = (in + 1) % capacidad;
        cantidad++;
        notifyAll(); 
    }

    public synchronized Evento retirar() throws InterruptedException {
        while (cantidad == 0) {
            wait(); 
        }
        Evento e = buffer[out];
        buffer[out] = null; 
        out = (out + 1) % capacidad;
        cantidad--;
        notifyAll(); 
        return e;
    }

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
