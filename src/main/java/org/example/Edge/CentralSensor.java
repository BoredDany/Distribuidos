package org.example.Edge;


import org.example.utils.TipoSensor;

public class CentralSensor {
    public static void main(String[] args) {

        // Crea y ejecuta 10 hilos para cada tipo de sensor
        int numSensores = 10;
        Thread[] threads = new Thread[numSensores];

        for (int i = 0; i < numSensores; i++) {
            threads[i] = new Thread(new SensorHandler(args[0], i, args[1]));
            threads[i].start();
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
