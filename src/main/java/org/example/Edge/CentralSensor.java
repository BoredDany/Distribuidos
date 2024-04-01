package org.example.Edge;


public class CentralSensor {
    public static void main(String[] args) {

        // Crea y ejecuta 10 hilos para cada tipo de sensor
        Thread[] threads = new Thread[30];
        String[] tiposSensores = {TipoSensor.HUMO, TipoSensor.HUMEDAD, TipoSensor.TEMPERATURA};

        for (String tipo : tiposSensores) {
            for (int i = 0; i < 1; i++) {
                threads[i] = new Thread(new SensorHandler(tipo, i));
                threads[i].start();
            }
        }

        // Espera a que todos los hilos terminen
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
