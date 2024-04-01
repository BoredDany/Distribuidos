package org.example.Edge;

import java.io.*;
import java.util.List;

public abstract class Sensor {
    private String tipoSensor;
    private Integer intervalo;
    private String archivoConfig;
    private Integer probDentroRango;
    private Integer probFueraRango;
    private Integer probError;
    private double limiteInferior;
    private double limiteSuperior;

    public Sensor() { }

    public Integer getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Integer intervalo) {
        this.intervalo = intervalo;
    }

    public Sensor(String tipoSensor, String archivoConfig) {
        this.tipoSensor = tipoSensor;
        this.archivoConfig = archivoConfig;
    }

    public void setLimiteInferior(double limiteInferior) {
        this.limiteInferior = limiteInferior;
    }

    public void setLimiteSuperior(double limiteSuperior) {
        this.limiteSuperior = limiteSuperior;
    }

    public double getLimiteInferior() {
        return limiteInferior;
    }

    public double getLimiteSuperior() {
        return limiteSuperior;
    }

    public String getTipoSensor() {
        return tipoSensor;
    }

    public String getArchivoConfig() {
        return archivoConfig;
    }

    public double getProbDentroRango() {
        return probDentroRango;
    }

    public double getProbFueraRango() {
        return probFueraRango;
    }

    public double getProbError() {
        return probError;
    }


    public void inicializar(){
        try (BufferedReader br = new BufferedReader(new FileReader(this.archivoConfig))) {
            String linea;
            int contador = 0;
            Integer[] numeros = new Integer[3];

            // Lee cada línea del archivo
            while ((linea = br.readLine()) != null && contador < 3) {
                Integer numero = Integer.parseInt(linea);
                numeros[contador] = numero;
                contador++;
            }

            //asigna probabilidades al sensor
            this.probDentroRango = numeros[0];
            this.probFueraRango = numeros[1];
            this.probError = numeros[2];

        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error: el archivo contiene un formato no válido.");
        }
    }

    public void enviarMedicionProxy(){

    }

    public abstract double generarMedicion(Integer dentroRango, Integer fueraRango, Integer erroreno);
    public abstract double generarCorrecta();
    public abstract double generarFueraDeRango();
    public abstract double generarErronea();
}