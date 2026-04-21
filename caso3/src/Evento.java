
public class Evento {

    private final int sensorId;      
    private final int secuencial;    
    private final int tipo;          
    private final boolean esFin;   

    public Evento(int sensorId, int secuencial, int tipo) {
        this.sensorId = sensorId;
        this.secuencial = secuencial;
        this.tipo = tipo;
        this.esFin = false;
    }

    //Constructor eventos fin
    private Evento() {
        this.sensorId = -1;
        this.secuencial = -1;
        this.tipo = -1;
        this.esFin = true;
    }

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
