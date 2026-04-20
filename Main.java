public class Main {

    public static void main(String[] args) throws InterruptedException {

        Buzon buzonEntrada = new Buzon();

        int baseEventos = 3;
        int ns = 3;

        Sensor s1 = new Sensor(1, baseEventos, ns, buzonEntrada);
        Sensor s2 = new Sensor(2, baseEventos, ns, buzonEntrada);
        Sensor s3 = new Sensor(3, baseEventos, ns, buzonEntrada);

        s1.start();
        s2.start();
        s3.start();

        s1.join();
        s2.join();
        s3.join();

        System.out.println("Todos los sensores acabaron");
    }
    
}
