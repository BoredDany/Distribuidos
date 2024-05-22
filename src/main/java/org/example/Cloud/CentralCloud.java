package org.example.Cloud;

import org.example.utils.Ip;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CentralCloud {
    private static final List<Double> SumasHumedad = new ArrayList<>();
    private static int calculoNumero = 1;

    public static void main(String[] args) throws Exception {
        Cloud cloud = new Cloud(Ip.CLOUD, Ip.PROXY_PRINCIPAL, Ip.SC_CLOUD, 20);
        try (ZContext context = new ZContext()) {
            // Socket para comunicación con proxy (REPLY)
            ZMQ.Socket socketCloud = context.createSocket(SocketType.REP);
            socketCloud.bind("tcp://" + cloud.getIpProxy() + ":" + Ip.PORT_PROXY_CLOUD);

            // Configurar el temporizador para calcular el promedio cada 20 segundos
            Timer timer = new Timer(true);

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    calcularPromedioHumedad();
                }
            }, 20000, 20000);

            while (!Thread.currentThread().isInterrupted()) {
                // Bloqueo hasta que se reciba un mensaje
                byte[] reply = socketCloud.recv(0);

                // Convertir el mensaje de byte array a String
                String mensaje = new String(reply, ZMQ.CHARSET);
                System.out.println("Received: [" + mensaje + "]");

                // Procesar el mensaje como cadena
                try {
                    procesarMensaje(mensaje);
                } catch (Exception e) {
                    System.err.println("Error al procesar el mensaje: " + e.getMessage());
                }

                // Responder a sensor
                String response = "Recibido en nube";
                socketCloud.send(response.getBytes(ZMQ.CHARSET), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para procesar el mensaje recibido como cadena
    private static void procesarMensaje(String mensaje) {
        if (mensaje.contains("promedio_humedad")) {
            String[] partes = mensaje.split(" ");
            if (partes.length >= 3) {
                // En la posición 3 del mensaje se encuentra la medición a manipular
                String valorStr = partes[2];
                try {
                    double valor = Double.parseDouble(valorStr);
                    // Agregar valor a lista temporal
                    SumasHumedad.add(valor);
                    calcularPromedioHumedad();

                } catch (NumberFormatException e) {
                    System.err.println("Error: el valor de humedad no es un número válido.");
                }
            }
        }
    }

    // Método para calcular el promedio de humedad
    private static void calcularPromedioHumedad() {
        double suma = 0.0;
        int cantidad = 0;

        for (double valor : SumasHumedad) {
            suma += valor;
            cantidad++;
        }

        if (cantidad > 0) {
            double promedio = suma / cantidad;
            System.out.println("HUMEDAD RELATIVA MENSUAL #" + (calculoNumero++) + ": " + promedio);
        } else {
            System.out.println("HUMEDAD RELATIVA MENSUAL #" + (calculoNumero++) + ": No hay datos");
        }
    }
}
