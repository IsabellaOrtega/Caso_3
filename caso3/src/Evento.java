/**
 * Representa un evento en el sistema IoT.
 *
 * Un evento puede ser:
 *   - Un evento normal, generado por un sensor con identificador unico y
 *     un tipo que indica el servidor de consolidacion destino.
 *   - Un evento de fin (marcador), usado para senalizar terminacion a los
 *     diferentes actores del sistema.
 *
 * Los eventos son inmutables una vez creados.
 */
public class Evento {

    private final int sensorId;      // Id del sensor que genero el evento (-1 si es de fin)
    private final int secuencial;    // Secuencial del evento dentro del sensor
    private final int tipo;          // Tipo del evento (1..ns) determina el servidor destino
    private final boolean esFin;     // true si es un evento de fin (marcador de terminacion)

    /** Constructor para un evento normal generado por un sensor. */
    public Evento(int sensorId, int secuencial, int tipo) {
        this.sensorId = sensorId;
        this.secuencial = secuencial;
        this.tipo = tipo;
        this.esFin = false;
    }

    /** Constructor privado para eventos de fin. */
    private Evento() {
        this.sensorId = -1;
        this.secuencial = -1;
        this.tipo = -1;
        this.esFin = true;
    }

    /** Construye un evento de fin (marcador de terminacion). */
    public static Evento eventoFin() {
        return new Evento();
    }

    public int getSensorId() {
        return sensorId;
    }

    public int getSecuencial() {
        return secuencial;
    }

    public int getTipo() {
        return tipo;
    }

    public boolean esFin() {
        return esFin;
    }

    /** Identificador unico del evento: "s<sensor>-<secuencial>". */
    public String getId() {
        if (esFin) {
            return "FIN";
        }
        return "s" + sensorId + "-" + secuencial;
    }

    @Override
    public String toString() {
        if (esFin) {
            return "[FIN]";
        }
        return "[" + getId() + ", tipo=" + tipo + "]";
    }
}
