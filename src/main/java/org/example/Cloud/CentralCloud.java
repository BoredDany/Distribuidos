package org.example.Cloud;

import org.example.utils.Ip;
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
        Cloud cloud = new Cloud(Ip.IP_CLOUD, Ip.IP_FOG, Ip.IP_CLOUD, 20);
        try (ZContext context = new ZContext()) {
            // Socket para comunicación con proxy (REPLY)
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind("tcp://" + cloud.getIpProxy() + ":" + Ip.PORT_PROXY_CLOUD);

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
                byte[] reply = socket.recv(0);

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
                socket.send(response.getBytes(ZMQ.CHARSET), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para procesar el mensaje recibido como cadena
    private static void procesarMensaje(String mensaje) {
        // Verificar si el mensaje contiene el tipo de sensor correcto
        if (mensaje.contains("\"tipoSensor\":\"promedio_humedad\"")) {
            // Extraer el valor de medición del mensaje
            String[] partes = mensaje.split(",");
            for (String parte : partes) {
                if (parte.contains("\"medicion\":")) {
                    String valorStr = parte.split(":")[1];
                    valorStr = valorStr.replaceAll("[^0-9.]", ""); // Eliminar caracteres no numéricos
                    try {
                        double valor = Double.parseDouble(valorStr);
                        // Synchronized para agregar valor a la lista de forma segura
                        synchronized (SumasHumedad) {
                            SumasHumedad.add(valor);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error: el valor de humedad no es un número válido.");
                    }
                    break;
                }
            }
        }
    }

    // Método para calcular el promedio de humedad
    private static void calcularPromedioHumedad() {
        double suma = 0.0;
        int cantidad = 0;

        // Synchronized para calcular y limpiar la lista de forma segura
        synchronized (SumasHumedad) {
            for (double valor : SumasHumedad) {
                suma += valor;
                cantidad++;
            }
            SumasHumedad.clear();
        }

        if (cantidad > 0) {
            double promedio = suma / cantidad;
            System.out.println("HUMEDAD RELATIVA MENSUAL #" + (calculoNumero++) + ": " + promedio);
        } else {
            System.out.println("HUMEDAD RELATIVA MENSUAL #" + (calculoNumero++) + ": No hay datos");
        }
    }
}
