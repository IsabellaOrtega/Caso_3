import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 Parametros esperados:
  ni    = numero de sensores
 base  = numero base de eventos
 nc    = numero de clasificadores
 ns    = numero de servidores de consolidacion
tam1  = capacidad del buzon de clasificacion
 tam2  = capacidad de cada buzon de consolidacion
 */
public class Config {

    public final int ni;
    public final int base;
    public final int nc;
    public final int ns;
    public final int tam1;
    public final int tam2;

    private Config(int ni, int base, int nc, int ns, int tam1, int tam2) {
        this.ni = ni;
        this.base = base;
        this.nc = nc;
        this.ns = ns;
        this.tam1 = tam1;
        this.tam2 = tam2;
    }

    public static Config leer(String ruta) throws IOException {
        Integer ni = null, base = null, nc = null, ns = null, tam1 = null, tam2 = null;

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            int numLinea = 0;
            while ((linea = br.readLine()) != null) {
                numLinea++;
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue;
                }
                int igual = linea.indexOf('=');
                if (igual < 0) {
                    throw new IOException("Linea " + numLinea + " mal formada (falta '='): " + linea);
                }
                String clave = linea.substring(0, igual).trim().toLowerCase();
                String valor = linea.substring(igual + 1).trim();
                int v;
                try {
                    v = Integer.parseInt(valor);
                } catch (NumberFormatException nfe) {
                    throw new IOException("Linea " + numLinea + " valor no numerico: " + valor);
                }
                switch (clave) {
                    case "ni":   ni = v; break;
                    case "base": base = v; break;
                    case "nc":   nc = v; break;
                    case "ns":   ns = v; break;
                    case "tam1": tam1 = v; break;
                    case "tam2": tam2 = v; break;
                    default:
                        System.err.println("Aviso: parametro desconocido en linea " + numLinea + ": " + clave);
                }
            }
        }

        if (ni == null || base == null || nc == null || ns == null || tam1 == null || tam2 == null) {
            throw new IOException("Archivo de configuracion incompleto. Se requieren: ni, base, nc, ns, tam1, tam2.");
        }
        if (ni <= 0 || base <= 0 || nc <= 0 || ns <= 0 || tam1 <= 0 || tam2 <= 0) {
            throw new IOException("Todos los parametros deben ser enteros positivos.");
        }
        return new Config(ni, base, nc, ns, tam1, tam2);
    }

    public int totalEventosEsperados() {
        return base * ni * (ni + 1) / 2;
    }

    @Override
    public String toString() {
        return "Config[ni=" + ni + ", base=" + base + ", nc=" + nc
                + ", ns=" + ns + ", tam1=" + tam1 + ", tam2=" + tam2
                + ", totalEventos=" + totalEventosEsperados() + "]";
    }
}
