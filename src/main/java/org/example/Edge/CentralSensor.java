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

        ZContext context = new ZContext();

        // Crea y ejecuta 10 hilos para cada tipo de sensor
        Thread[] threads = new Thread[30];
        //String[] tiposSensores = {TipoSensor.HUMO, TipoSensor.HUMEDAD, TipoSensor.TEMPERATURA};

        String[] tiposSensores = {TipoSensor.HUMO};

        for (String tipo : tiposSensores) {
            for (int i = 0; i < 2; i++) {
                threads[i] = new Thread(new SensorHandler(tipo, context));
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

        context.close();

    }
}
