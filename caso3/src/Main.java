import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;


public class Main {

    public static void main(String[] args) {
        String rutaConfig = args.length > 0 ? args[0] : "config.txt";

        Config cfg;
        try {
            cfg = Config.leer(rutaConfig);
        } catch (Exception ex) {
            System.err.println("Error leyendo configuracion (" + rutaConfig + "): " + ex.getMessage());
            System.exit(1);
            return;
        }

        System.out.println("== Simulador IoT - Caso 3 ==");
        System.out.println(cfg);
        System.out.println();

        final long tiempoInicio = System.currentTimeMillis();


        BuzonIlimitado buzonEntrada   = new BuzonIlimitado("Entrada");
        BuzonIlimitado buzonAlertas   = new BuzonIlimitado("Alertas");
        BuzonAcotado   buzonClasif    = new BuzonAcotado("Clasificacion", cfg.tam1);
        BuzonAcotado[] buzonesConsol  = new BuzonAcotado[cfg.ns];
        for (int i = 0; i < cfg.ns; i++) {
            buzonesConsol[i] = new BuzonAcotado("Consolidacion-" + (i + 1), cfg.tam2);
        }


        ContadorClasificadores contador = new ContadorClasificadores(cfg.nc);


        CyclicBarrier barreraSensores = new CyclicBarrier(cfg.ni);
        List<Sensor> sensores = new ArrayList<>();
        int totalEventos = cfg.totalEventosEsperados();
        for (int i = 1; i <= cfg.ni; i++) {
            int numEventos = cfg.base * i;
            sensores.add(new Sensor(i, numEventos, cfg.ns, buzonEntrada, barreraSensores, 1000L + i));
        }
        Broker broker = new Broker(totalEventos, buzonEntrada, buzonAlertas, buzonClasif, 2000L);
        Administrador admin = new Administrador(cfg.nc, buzonAlertas, buzonClasif, 3000L);
        List<Clasificador> clasificadores = new ArrayList<>();
        for (int i = 1; i <= cfg.nc; i++) {
            clasificadores.add(new Clasificador(i, buzonClasif, buzonesConsol, contador));
        }
        List<Servidor> servidores = new ArrayList<>();
        for (int i = 1; i <= cfg.ns; i++) {
            servidores.add(new Servidor(i, buzonesConsol[i - 1], 4000L + i));
        }

        for (Servidor s : servidores) s.start();
        for (Clasificador c : clasificadores) c.start();
        admin.start();
        broker.start();
        for (Sensor s : sensores) s.start();


        try {
            for (Sensor s : sensores) s.join();
            broker.join();
            admin.join();
            for (Clasificador c : clasificadores) c.join();
            for (Servidor s : servidores) s.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.err.println("Main interrumpido esperando threads.");
            return;
        }

        final long tiempoFin = System.currentTimeMillis();

        boolean ok = true;
        System.out.println();
        System.out.println("== Validacion final del sistema ==");
        ok &= verificarVacio(buzonEntrada.getNombre(), buzonEntrada.tamano());
        ok &= verificarVacio(buzonAlertas.getNombre(), buzonAlertas.tamano());
        ok &= verificarVacio(buzonClasif.getNombre(), buzonClasif.tamano());
        for (BuzonAcotado b : buzonesConsol) {
            ok &= verificarVacio(b.getNombre(), b.tamano());
        }
        System.out.println("Total eventos esperados: " + totalEventos);
        System.out.println("Duracion: " + (tiempoFin - tiempoInicio) + " ms");
        System.out.println("Estado final: " + (ok ? "OK (todos los buzones vacios)" : "ERROR"));
        System.exit(ok ? 0 : 2);
    }

    private static boolean verificarVacio(String nombre, int tamano) {
        boolean ok = tamano == 0;
        System.out.println("  Buzon " + nombre + ": tamano=" + tamano + " -> " + (ok ? "OK" : "ERROR"));
        return ok;
    }
}
