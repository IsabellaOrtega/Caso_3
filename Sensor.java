
import java.util.Random;

public class Sensor extends Thread {
    private int id;
    private int numeroEventos;
    private int ns;
    private Random random;
    private Buzon buzonEntrada;

    public Sensor(int id, int baseEventos, int ns, Buzon buzonEntrada) {
        this.id = id;
        this.numeroEventos = baseEventos * id; 
        this.ns = ns;
        this.random = new Random();
        this.buzonEntrada = buzonEntrada;
    }

    @Override
    public void run() {
        for (int i = 1; i <= numeroEventos; i++) {

            int tipo = random.nextInt(ns) + 1;

            String idEvento = "S" + id + "-E" + i;

            Evento evento = new Evento(idEvento, tipo, false);
            buzonEntrada.depositar(evento);

            System.out.println("Sensor " + id + " envió: " + evento);
        }
    }}
