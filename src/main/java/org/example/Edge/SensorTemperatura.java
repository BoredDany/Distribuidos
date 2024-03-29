package org.example.Edge;

import zmq.socket.Pair;

public class SensorTemperatura extends Sensor{


    public SensorTemperatura() {
    }

    public SensorTemperatura(String tipoSensor, String archivoConfig, Pair intervaloMedicion) {
        super(tipoSensor, archivoConfig, intervaloMedicion);
    }

    @Override
    public void inicializar(String tipo, String archivoConfig, Pair intervaloMedicion) {
        super.inicializar(tipo, archivoConfig, intervaloMedicion);
        tipo = "Temperatura";
        archivoConfig = "";
        //intervaloMedicion = new Pair(0,1);
    }
}
