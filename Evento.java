public class Evento {
    
    private String id;
    private int tipo;
    private boolean esEventoFin;

    public Evento(String id, int tipo, boolean esEventoFin) {
        this.id = id;
        this.tipo = tipo;
        this.esEventoFin = esEventoFin;
    }

    public String getid(){
        return id;
    }

    public int gettipo(){
        return tipo;
    }

    public boolean getesEventoFin(){
        return esEventoFin;
    }

}
