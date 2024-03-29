package org.example.Edge;

import zmq.socket.Pair;

public class SensorHumedad extends Sensor{


    public SensorHumedad() {
    }

    public SensorHumedad(String tipoSensor, String archivo, Pair intervaloMedicion) {
        super(tipoSensor, archivo, intervaloMedicion);
    }

    @Override
    public void inicializar(String tipo, String archivoConfig, Pair intervaloMedicion) {
        super.inicializar(tipo, archivoConfig, intervaloMedicion);
        tipo = "Humedad";
        archivoConfig = "";
        //intervaloMedicion = new Pair(0,1);
    }
}
