package org.example.Edge;

import zmq.socket.Pair;


public class Sensor {

    //padre de sensores (humedad-temperatura-humo)

    private String tipoSensor;

    private String archivoConfig;

    private Pair intervaloMedicion;


    public Sensor() { }

    public Sensor(String tipoSensor, String archivoConfig, Pair intervaloMedicion) {
        this.tipoSensor = tipoSensor;
        this.archivoConfig = archivoConfig;
        this.intervaloMedicion = intervaloMedicion;
    }

    public String getTipoSensor() {
        return tipoSensor;
    }

    public void setTipoSensor(String tipoSensor) {
        this.tipoSensor = tipoSensor;
    }

    public String getArchivoConfig() {
        return archivoConfig;
    }

    public void setArchivoConfig(String archivo) {
        this.archivoConfig = archivo;
    }

    public Pair getIntervaloMedicion() {
        return intervaloMedicion;
    }

    public void setIntervaloMedicion(Pair intervaloMedicion) {
        this.intervaloMedicion = intervaloMedicion;
    }

    public void inicializar(String tipo, String archivoConfig, Pair intervaloMedicion){

    }


    public double generarMedicion(){
        return Math.random();
    }

    public void enviarMedicionProxy(){

    }

}
