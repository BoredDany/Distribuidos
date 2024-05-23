package org.example.Cloud;

import org.example.utils.Checkeo;
import org.example.utils.Ip;
import org.example.utils.Medicion;
import org.example.utils.TipoSensor;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

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
            socket.setReceiveTimeOut(3000);

            // Socket para comunicación con sistema de calidad (REQUEST)
            ZMQ.Socket socketSistemaCalidad = context.createSocket(ZMQ.REQ);
            socketSistemaCalidad.connect("tcp://" + Ip.IP_CLOUD + ":" + Ip.PORT_SC_CLOUD);

            // Socket de comunicacion con checker (SUB)
            ZMQ.Socket subscriber = context.createSocket(ZMQ.SUB);
            subscriber.connect("tcp://" +  Ip.IP_CLOUD + ":" + Ip.PORT_CLOUD_CHECKER); // Cambia esto a la dirección y puerto que usaste en el HealthChecker
            subscriber.subscribe(ZMQ.SUBSCRIPTION_ALL);

            // Crear un objeto Poller y registrar los sockets
            ZMQ.Poller poller = context.createPoller(2);
            poller.register(socket, ZMQ.Poller.POLLIN);
            poller.register(subscriber, ZMQ.Poller.POLLIN);

            // Configurar el temporizador para calcular el promedio cada 20 segundos
            Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    calcularPromedioHumedad(socketSistemaCalidad);
                }
            }, 20000, 20000);

            while (!Thread.currentThread().isInterrupted()) {

                // Esperar hasta que se reciba un mensaje en cualquiera de los sockets, o hasta que transcurra un tiempo de espera
                int pollResult = poller.poll(1000); // Tiempo de espera de 1000 ms

                if (pollResult == -1) {
                    // Error en poll
                    System.out.println("error poll");
                    break;
                }

                if (poller.pollin(0)) {
                    //System.out.println("POLLIN PROXY");
                    // Se recibió un mensaje en el socket REP
                    if (socket.hasReceiveMore()) {
                        byte[] reply = socket.recv(ZMQ.DONTWAIT);
                        if (reply == null) {
                            // No hay mensajes disponibles, puedes hacer algo más aquí si lo deseas
                            continue;
                        } else {
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
                    }
                }

                if (poller.pollin(1)) {
                    // Se recibió un mensaje en el socket SUB
                    //Recibir mensajes del checker
                    String message = subscriber.recvStr(ZMQ.DONTWAIT);
                    if (message != null) {
                        Checkeo checkeo = Checkeo.fromJson(message); // Asume que tienes un método para convertir de JSON a Checkeo
                        Ip.IP_FOG = checkeo.getIp(); // Actualiza la IP del proxy
                        System.out.println("CAMBIO IP: " + checkeo.getIp());
                        System.out.println("+++++++++++++++++++++++++" +
                                "++++++++++++++++++++++++++++++ " + checkeo.getIp());

                        // Cerrar el socket existente
                        // Desregistrar el socket REP del poller antes de cerrarlo
                        poller.unregister(socket);
                        socket.close();

                        // Crear un nuevo socket con la nueva dirección IP
                        socket = context.createSocket(SocketType.REP);
                        socket.connect("tcp://" + Ip.IP_FOG + ":" + Ip.PORT_PROXY_CLOUD);
                        socket.setReceiveTimeOut(3000);
                        // Registrar el nuevo socket con el poller
                        poller.register(socket, ZMQ.Poller.POLLIN);
                    } else {
                        // No hay mensajes disponibles, puedes hacer algo más aquí si lo deseas
                    }
                }

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
}
