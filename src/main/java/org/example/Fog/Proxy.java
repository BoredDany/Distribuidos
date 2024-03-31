package org.example.Fog;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Proxy {

    private String ip;
    private Integer intervaloTemperatura;
    private Integer intervaloHumedad;
    private String ipChecker;

    public Proxy(String ip, Integer intervaloTemperatura, Integer intervaloHumedad, String ipChecker) {
        this.ip = ip;
        this.intervaloTemperatura = intervaloTemperatura;
        this.intervaloHumedad = intervaloHumedad;
        this.ipChecker = ipChecker;
    }

    public void start() {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind("tcp://" + ipChecker + ":5555");

            while (!Thread.currentThread().isInterrupted()) {
                byte[] message = socket.recv(0);
                String request = new String(message, ZMQ.CHARSET);
                System.out.println("Mensaje recibido por el servidor: " + request);

                if (request.equals("Verificar servidor")) {
                    socket.send("Servidor funcionando".getBytes(ZMQ.CHARSET), 0);
                } else {
                    // Procesa el mensaje recibido si es necesario
                    // Envía una respuesta al cliente si es necesario
                    System.out.println("hola");
                    //socket.send("Respuesta desde el servidor".getBytes(ZMQ.CHARSET), 0);
                }
            }
        }
    }
    public static void main(String[] args) throws Exception {
        Proxy proxy = new Proxy("192.168.20.8", 5, 5, "192.168.20.8");
        while (true){
            proxy.start();
            //recibir mediciones de sensores
        }

    }
}
