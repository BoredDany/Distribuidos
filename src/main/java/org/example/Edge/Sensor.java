package org.example.Edge;
import zmq.socket.Pair;
public class Sensor {
    //padre de sensores (humedad-temperatura-humo)
    private String tipoSensor;

    private String archivoConfig;

    private double limiteInferior;
    private double limiteSuperior;

    public Sensor() { }

    public Sensor(String tipoSensor, String archivoConfig, double limiteInferior, double limiteSuperior) {
        this.tipoSensor = tipoSensor;
        this.archivoConfig = archivoConfig;
        this.limiteInferior = limiteInferior;
        this.limiteSuperior = limiteSuperior;
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

    public double getlimiteInferior () {
        return limiteInferior;
    }

    public void setlimiteInferior(double limiteInferior) {
        this.limiteInferior = limiteInferior;
    }
    public double getLimiteSuperior () {
        return limiteSuperior;
    }
    public void setlimiteSuperior(double limiteSuperior) {
        this.limiteSuperior = limiteSuperior;
    }

    public void inicializar(String tipo, String archivoConfig, double limiteInferior, double limiteSuperior){

    }

    public double generarMedicion(){
        return 0.0;
    }

    public void enviarMedicionProxy(){

    }

}
