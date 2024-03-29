package org.example.Edge;

import zmq.socket.Pair;

public class SensorHumo extends Sensor {

    public SensorHumo() {
    }

    public SensorHumo(String tipoSensor, String archivoConfig, Pair intervaloMedicion) {
        super(tipoSensor, archivoConfig, intervaloMedicion);
    }

    @Override
    public void inicializar(String tipo, String archivoConfig, Pair intervaloMedicion) {
        super.inicializar(tipo, archivoConfig, intervaloMedicion);
        tipo = "Humo";
        archivoConfig = "";
        //intervaloMedicion = new Pair(0,1);


    }
}
