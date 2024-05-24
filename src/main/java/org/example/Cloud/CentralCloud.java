package org.example.Cloud;

import org.example.utils.Checkeo;
import org.example.utils.Ip;
import org.example.utils.Medicion;
import org.example.utils.TipoSensor;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
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
            socket.bind("tcp://" + Ip.IP_FOG + ":" + Ip.PORT_PROXY_CLOUD);

            // Socket para comunicación con sistema de calidad (REQUEST)
            ZMQ.Socket socketSistemaCalidad = context.createSocket(ZMQ.REQ);
            socketSistemaCalidad.connect("tcp://" + Ip.IP_CLOUD + ":" + Ip.PORT_SC_CLOUD);

            // Configurar el temporizador para calcular el promedio cada 20 segundos
            Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    calcularPromedioHumedad(socketSistemaCalidad);
                }
            }, 20000, 20000);

            while (!Thread.currentThread().isInterrupted()) {

                byte[] reply = socket.recv(0);
                // Procesar el mensaje recibido
                String mensaje = new String(reply, ZMQ.CHARSET);
                System.out.println("Received: [" + mensaje + "]");
                try {
                    procesarMensaje(mensaje);
                } catch (Exception e) {
                    System.err.println("Error al procesar el mensaje: " + e.getMessage());
                }
                // Responder al sensor
                String response = "Recibido en nube";
                socket.send(response.getBytes(ZMQ.CHARSET), 0);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para procesar el mensaje recibido como cadena
    private static void procesarMensaje(String mensaje) {
        //Guardar
        guardarMensaje("ALERTA" + mensaje);
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
    private static void calcularPromedioHumedad(ZMQ.Socket socketSistemaCalidad) {
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
            // Enviar alerta a sistemas de calidad
            if(promedio < TipoSensor.HUMEDAD_INFERIOR || promedio > TipoSensor.HUMEDAD_SUPERIOR){

                //GUARDAR
                guardarMensaje("ALERTA CLOUD: PROMEDIO HUMEDAD: " + promedio);
                System.out.println("ALERTA HUMEDAD: " + promedio);

                // Obtener la hora actual
                LocalTime now = LocalTime.now();
                String hora = now.getHour() + ":" + now.getMinute() + ":" + now.getSecond();

                Medicion medicion = new Medicion(TipoSensor.ALERTA_HUMEDAD, 0, promedio, hora, true, true);

                socketSistemaCalidad.send(medicion.toJson());
                byte[] response = socketSistemaCalidad.recv(0);
                System.out.println("Recibo del SC: " + new String(response, ZMQ.CHARSET));

            }
        } else {
            System.out.println("HUMEDAD RELATIVA MENSUAL #" + (calculoNumero++) + ": No hay datos");
        }
    }

    // Método para guardar el mensaje en un archivo
    private static void guardarMensaje(String mensaje) {
        try (FileWriter writer = new FileWriter("received_messages.txt", true)) {
            writer.write(mensaje + "\n");
        } catch (IOException e) {
            System.err.println("Error al guardar el mensaje en el archivo: " + e.getMessage());
        }
    }

    private static Medicion parseMedicionFromBytes(String mensaje) {
        try {
            return Medicion.fromJson(mensaje);
        } catch (Exception e) {
            System.err.println("Error al convertir el mensaje a objeto Medicion: " + e.getMessage());
            return null;
        }
    }
}
