import java.util.Random;
import java.util.concurrent.CyclicBarrier;


public class Sensor extends Thread {

    private final int id;                      
    private final int numEventos;              
    private final int ns;                      
    private final BuzonIlimitado buzonEntrada; 
    private final Random random;               
    private final CyclicBarrier barreraInicio; 

    public Sensor(int id, int numEventos, int ns,
                  BuzonIlimitado buzonEntrada,
                  CyclicBarrier barreraInicio,
                  long semilla) {
        this.id = id;
        this.numEventos = numEventos;
        this.ns = ns;
        this.buzonEntrada = buzonEntrada;
        this.barreraInicio = barreraInicio;
        this.random = new Random(semilla);
    }

    @Override
    public void run() {
        try {

            barreraInicio.await();

            for (int secuencial = 1; secuencial <= numEventos; secuencial++) {
                int tipo = random.nextInt(ns) + 1; // [1, ns]
                Evento e = new Evento(id, secuencial, tipo);
                buzonEntrada.depositar(e);


                Thread.yield();
            }
            System.out.println("[" + getName() + "] Termina: genero " + numEventos + " eventos.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
