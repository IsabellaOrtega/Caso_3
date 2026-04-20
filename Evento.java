public class Evento {
    
    private String id;
    private int tipo;
    private boolean esEventoFin;

    public Evento(String id, int tipo, boolean esEventoFin) {
        this.id = id;
        this.tipo = tipo;
        this.esEventoFin = esEventoFin;
    }

    @Override
    public String toString() {
        return id + " tipo:" + tipo;
    }

}