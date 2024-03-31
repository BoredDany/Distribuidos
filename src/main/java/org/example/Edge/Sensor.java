package org.example.Edge;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Sensor {
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

    public double getLimiteInferior () {
        return limiteInferior;
    }

    public void setLimiteInferior(double limiteInferior) {
        this.limiteInferior = limiteInferior;
    }
    public double getLimiteSuperior () {
        return limiteSuperior;
    }
    public void setLimiteSuperior(double limiteSuperior) {
        this.limiteSuperior = limiteSuperior;
    }

    public void inicializar(String tipo, String archivoConfig, double limiteInferior, double limiteSuperior){

    }

    public void enviarMedicionProxy(){

    }

    public List<String> leerArchivoConfig(String path) throws IOException {
        File file = new File(path);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        String line;
        List<String> data = new ArrayList<>();
        while((line = br.readLine()) != null){
            data.add(line);
        }

        br.close();
        fr.close();

        for (String info : data){
            System.out.println(info);
        }

        return data;
    }


    public abstract double generarMedicion() throws IOException;
}