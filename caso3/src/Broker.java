import java.util.Random;

// Broker procesa eventos y decide si son normales o anomalos
public class Broker extends Thread {

    private final int totalEventosEsperados;
    private final BuzonIlimitado buzonEntrada;
    private final BuzonIlimitado buzonAlertas;
    private final BuzonAcotado buzonClasificacion;
    private final Random random;

    public Broker(int totalEventosEsperados,
                  BuzonIlimitado buzonEntrada,
                  BuzonIlimitado buzonAlertas,
                  BuzonAcotado buzonClasificacion,
                  long semilla) {
        this.totalEventosEsperados = totalEventosEsperados;
        this.buzonEntrada = buzonEntrada;
        this.buzonAlertas = buzonAlertas;
        this.buzonClasificacion = buzonClasificacion;
        this.random = new Random(semilla);
    }

    @Override
    public void run() {
        int cont = 0;

        try {
            while (cont < totalEventosEsperados) {
                Evento e = buzonEntrada.retirar();

                int n = random.nextInt(201);

                if (n % 8 == 0) {
                    buzonAlertas.depositar(e);
                } else {
                    buzonClasificacion.depositar(e);
                }

                cont++;
            }

            buzonAlertas.depositar(Evento.eventoFin());
            System.out.println("Broker termino");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
