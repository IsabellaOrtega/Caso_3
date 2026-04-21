import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

/**
 * Punto de entrada del Simulador Concurrente de Sistema IoT (Caso 3).
 *
 * Responsabilidades:
 *   - Leer el archivo de configuracion.
 *   - Crear los buzones (uno de entrada, uno de alertas, uno de clasificacion,
 *     ns de consolidacion).
 *   - Crear e iniciar los threads (ni sensores, 1 broker, 1 administrador,
 *     nc clasificadores, ns servidores).
 *   - Esperar (join) a que todos los threads terminen.
 *   - Validar el estado final del sistema (buzones vacios).
 *
 * Uso:
 *   java Main [ruta_config]
 *   Por defecto, la ruta es "config.txt".
 */
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

        // ---- Creacion de los buzones ----
        BuzonIlimitado buzonEntrada   = new BuzonIlimitado("Entrada");
        BuzonIlimitado buzonAlertas   = new BuzonIlimitado("Alertas");
        BuzonAcotado   buzonClasif    = new BuzonAcotado("Clasificacion", cfg.tam1);
        BuzonAcotado[] buzonesConsol  = new BuzonAcotado[cfg.ns];
        for (int i = 0; i < cfg.ns; i++) {
            buzonesConsol[i] = new BuzonAcotado("Consolidacion-" + (i + 1), cfg.tam2);
        }

        // ---- Contador para identificar al ultimo clasificador ----
        ContadorClasificadores contador = new ContadorClasificadores(cfg.nc);

        // ---- Creacion de los threads ----
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

        // ---- Inicio de los threads ----
        // Consumidores primero, para que esten listos cuando lleguen eventos.
        for (Servidor s : servidores) s.start();
        for (Clasificador c : clasificadores) c.start();
        admin.start();
        broker.start();
        for (Sensor s : sensores) s.start();

        // ---- Esperar terminacion (join) ----
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

        // ---- Validacion final: todos los buzones deben quedar vacios ----
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
