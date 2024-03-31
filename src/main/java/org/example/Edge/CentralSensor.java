package org.example.Edge;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;


public class CentralSensor {
    public static void main(String[] args) {

        Random random = new Random();

        List<SensorHumo> sensoresHumo = new ArrayList<>();
        List<Double> probabilidadesSH = new ArrayList<>();

        // Crear instancias de Sensores de Humo
        for (int i = 0; i < 10; i++) {
            SensorHumo sh = new SensorHumo();
            sensoresHumo.add(sh);
            sh.setTipoSensor("Humo");
            sh.setArchivoConfig("configHumo.txt");
            sh.setLimiteInferior(0.0);
            sh.setLimiteSuperior(1.0);
            if (i == 0) {
                try (BufferedReader br = new BufferedReader(new FileReader(sh.getArchivoConfig()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        try {
                            double numero = Double.parseDouble(line.trim());
                            probabilidadesSH.add(numero);
                        } catch (NumberFormatException e) {
                            System.err.println("Ignorando línea no válida: " + line);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Crear instancias de Sensores de Humedad
        List<SensorHumedad> sensoresHumedad = new ArrayList<>();
        List<Double> probabilidadesSHu = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SensorHumedad shu = new SensorHumedad();
            sensoresHumedad.add(shu);
            shu.setTipoSensor("Humedad");
            shu.setArchivoConfig("configHumedad.txt");
            shu.setLimiteInferior(70.0);
            shu.setLimiteSuperior(100.0);
            if (i == 0) {
                try (BufferedReader br = new BufferedReader(new FileReader(shu.getArchivoConfig()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        try {
                            double numero = Double.parseDouble(line.trim());
                            probabilidadesSHu.add(numero);
                        } catch (NumberFormatException e) {
                            System.err.println("Ignorando línea no válida: " + line);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Crear instancias de Sensores de Temperatura
        List<SensorTemperatura> sensoresTemperatura = new ArrayList<>();
        List<Double> probabilidadesSHt = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SensorTemperatura st = new SensorTemperatura();
            sensoresTemperatura.add(st);
            st.setTipoSensor("Temperatura");
            st.setArchivoConfig("configTemperatura.txt");
            st.setLimiteInferior(11.0);
            st.setLimiteSuperior(29.4);
            if (i == 0) {
                try (BufferedReader br = new BufferedReader(new FileReader("configTemperatura.txt"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        try {
                            double numero = Double.parseDouble(line.trim());
                            probabilidadesSHt.add(numero);
                        } catch (NumberFormatException e) {
                            System.err.println("Ignorando línea no válida: " + line);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Aquí se realiza la comunicación con el proxy
        try (ZContext context = new ZContext()) {
            // Crear un socket para comunicarse con el proxy
            ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
            socket.connect("tcp://localhost:5000");

            // Enviar las mediciones de los sensores de humo al proxy
            for (SensorHumo sh : sensoresHumo) {
                int medicionesDentroRango = (int) (probabilidadesSH.get(0) * 10);
                int medicionesFueraRangoPositivas = (int) (probabilidadesSH.get(1) * 10);
                int medicionesFueraRangoNegativas = (int) (probabilidadesSH.get(2) * 10);

                // Lista de tipos de mediciones ponderadas según las probabilidades
                List<String> tiposMediciones = new ArrayList<>();
                for (int i = 0; i < medicionesDentroRango; i++) {
                    tiposMediciones.add("Dentro del rango");
                }
                for (int i = 0; i < medicionesFueraRangoPositivas; i++) {
                    tiposMediciones.add("Fuera del rango, positiva");
                }
                for (int i = 0; i < medicionesFueraRangoNegativas; i++) {
                    tiposMediciones.add("Fuera del rango, negativa");
                }

                // Barajar la lista para obtener un orden aleatorio
                Collections.shuffle(tiposMediciones, random);

                // Realizar las mediciones
                for (String tipoMedicion : tiposMediciones) {
                    double medicion;
                    String mensaje;

                    switch (tipoMedicion) {
                        case "Dentro del rango":
                            if (random.nextBoolean()) {
                                medicion = sh.getLimiteInferior();
                            } else {
                                medicion = sh.getLimiteSuperior();
                            }
                            mensaje = "Medicion Humo (Dentro del rango): " + medicion;
                            break;
                        case "Fuera del rango, positiva":
                            medicion = random.nextDouble() * (100 - sh.getLimiteSuperior()) + sh.getLimiteSuperior();
                            mensaje = "Medicion Humo (Fuera del rango, positiva): " + medicion;
                            break;
                        case "Fuera del rango, negativa":
                            medicion = -random.nextDouble() * 9.9; // Número negativo con una cifra decimal
                            medicion = Math.round(medicion);
                            mensaje = "Medicion Humo (Fuera del rango, negativa): " + medicion;
                            break;
                        default:
                            continue;
                    }

                    socket.send(mensaje.getBytes(ZMQ.CHARSET), 0);
                    byte[] respuesta = socket.recv(0);
                    System.out.println("Respuesta del proxy: " + new String(respuesta, ZMQ.CHARSET));
                }

                break; // Salir después de procesar las mediciones de un sensor (solo para el primer sensor)
            }

            // Enviar las mediciones de los sensores de humedad al proxy
            for (SensorHumedad shu : sensoresHumedad) {
                int medicionesDentroRango = (int) (probabilidadesSHu.get(0) * 10);
                int medicionesFueraRangoPositivas = (int) (probabilidadesSHu.get(1) * 10);
                int medicionesFueraRangoNegativas = (int) (probabilidadesSHu.get(2) * 10);

                // Lista de tipos de mediciones ponderadas según las probabilidades
                List<String> tiposMediciones = new ArrayList<>();
                for (int i = 0; i < medicionesDentroRango; i++) {
                    tiposMediciones.add("Dentro del rango");
                }
                for (int i = 0; i < medicionesFueraRangoPositivas; i++) {
                    tiposMediciones.add("Fuera del rango, positiva");
                }
                for (int i = 0; i < medicionesFueraRangoNegativas; i++) {
                    tiposMediciones.add("Fuera del rango, negativa");
                }

                // Barajar la lista para obtener un orden aleatorio
                Collections.shuffle(tiposMediciones, random);

                // Realizar las mediciones
                for (String tipoMedicion : tiposMediciones) {
                    double medicion;
                    String mensaje;

                    switch (tipoMedicion) {
                        case "Dentro del rango":
                            medicion = random.nextDouble() * (shu.getLimiteSuperior() - shu.getLimiteInferior()) + shu.getLimiteInferior();
                            medicion = Math.round(medicion * 10.0) / 10.0;
                            mensaje = "Medicion Humedad (Dentro del rango): " + medicion;
                            break;
                        case "Fuera del rango, positiva":
                            medicion = random.nextDouble() * (shu.getLimiteSuperior()) - shu.getLimiteSuperior();
                            medicion += shu.getLimiteInferior();
                            medicion = Math.max(medicion, 2.7);
                            medicion = Math.round(medicion * 10.0) / 10.0;
                            mensaje = "Medicion Humedad  (Fuera del rango, positiva): " + medicion;
                            break;
                        case "Fuera del rango, negativa":
                            medicion = -random.nextDouble() * 9.9; // Número negativo con una cifra decimal
                            medicion = Math.round(medicion * 10.0) / 10.0;
                            mensaje = "Medicion Humedad (Fuera del rango, negativa): " + medicion;
                            break;
                        default:
                            continue;
                    }

                    socket.send(mensaje.getBytes(ZMQ.CHARSET), 0);
                    byte[] respuesta = socket.recv(0);
                    System.out.println("Respuesta del proxy: " + new String(respuesta, ZMQ.CHARSET));
                }

                break; // Salir después de procesar las mediciones de un sensor (solo para el primer sensor)
            }

            // Enviar las mediciones de los sensores de temperatura al proxy
            for (SensorTemperatura st : sensoresTemperatura) {
                int medicionesDentroRango = (int) (probabilidadesSHt.get(0) * 10);
                int medicionesFueraRangoPositivas = (int) (probabilidadesSHt.get(1) * 10);
                int medicionesFueraRangoNegativas = (int) (probabilidadesSHt.get(2) * 10);

                // Lista de tipos de mediciones ponderadas según las probabilidades
                List<String> tiposMediciones = new ArrayList<>();
                for (int i = 0; i < medicionesDentroRango; i++) {
                    tiposMediciones.add("Dentro del rango");
                }
                for (int i = 0; i < medicionesFueraRangoPositivas; i++) {
                    tiposMediciones.add("Fuera del rango, positiva");
                }
                for (int i = 0; i < medicionesFueraRangoNegativas; i++) {
                    tiposMediciones.add("Fuera del rango, negativa");
                }

                // Barajar la lista para obtener un orden aleatorio
                Collections.shuffle(tiposMediciones, random);

                // Realizar las mediciones
                for (String tipoMedicion : tiposMediciones) {
                    double medicion;
                    String mensaje;

                    switch (tipoMedicion) {
                        case "Dentro del rango":
                            medicion = random.nextDouble() * (st.getLimiteSuperior() - st.getLimiteInferior()) + st.getLimiteInferior();
                            medicion = Math.round(medicion * 10.0) / 10.0;
                            mensaje = "Medicion Temperatura (Dentro del rango): " + medicion;
                            break;
                        case "Fuera del rango, positiva":
                            medicion = random.nextDouble() * 10 * (st.getLimiteSuperior()) - st.getLimiteSuperior();
                            medicion += st.getLimiteInferior();
                            medicion = Math.max(medicion, 37.8);
                            medicion = Math.round(medicion * 10.0) / 10.0;
                            mensaje = "Medicion Temperatura  (Fuera del rango, positiva): " + medicion;
                            break;
                        case "Fuera del rango, negativa":
                            medicion = -random.nextDouble() * 9.9; // Número negativo con una cifra decimal
                            medicion = Math.round(medicion * 10.0) / 10.0;
                            mensaje = "Medicion Temperatura (Fuera del rango, negativa): " + medicion;
                            break;
                        default:
                            continue;
                    }

                    socket.send(mensaje.getBytes(ZMQ.CHARSET), 0);
                    byte[] respuesta = socket.recv(0);
                    System.out.println("Respuesta del proxy: " + new String(respuesta, ZMQ.CHARSET));
                }

                break; // Salir después de procesar las mediciones de un sensor (solo para el primer sensor)
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
