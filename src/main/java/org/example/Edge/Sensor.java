package org.example.Edge;

import java.io.*;
import java.util.List;
import java.util.Random;

public abstract class Sensor {
    private Integer id;
    private String tipoSensor;
    private Integer intervalo;
    private String archivoConfig;
    private Integer probDentroRango;
    private Integer probFueraRango;
    private Integer probError;
    private double limiteInferior;
    private double limiteSuperior;


    public Sensor(Integer id, String tipoSensor, String archivoConfig) {
        this.id = id;
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

    public Integer getIntervalo() {
        return intervalo;
    }

    public Integer getId() {
        return id;
    }

    public void setIntervalo(Integer intervalo) {
        this.intervalo = intervalo;
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

    public double generarMedicion(Integer dentroRango, Integer fueraRango, Integer erroreno) {
        double medicion = -1;

        if(dentroRango == 0 && fueraRango == 0 && erroreno == 0){
            //si están vacias retornar una correcta
            medicion = generarCorrecta();
        }else{
            //si no calcular porcentajes
            double pDentroRango = (this.getProbDentroRango() / 100);
            double pFueraRango = (this.getProbFueraRango() / 100);
            double pErroneo = (this.getProbError() / 100);

            //calcular proporciones
            Integer total = dentroRango + fueraRango + erroreno;
            double porcentajeDentroRango = (double) dentroRango / total;
            double porcentajeFueraRango = (double) fueraRango / total;
            double porcentajeErroneo = (double) erroreno / total;

            //calcular diferencias
            double diferenciaDentroRango = pDentroRango - porcentajeDentroRango;
            double diferenciaFueraRango = pFueraRango - porcentajeFueraRango;
            double diferenciaErroneo = pErroneo - porcentajeErroneo;

            //si las proporciones se mantienen retornar una correcta
            if(pDentroRango == porcentajeDentroRango && pFueraRango == porcentajeFueraRango && pErroneo == porcentajeErroneo){
                medicion = generarCorrecta();
            }else{//si no ver cual proporción es menor a lo que debería y de esa generar un valor
                double diferenciaMaxima = Math.max(diferenciaDentroRango, Math.max(diferenciaFueraRango, diferenciaErroneo));
                if(diferenciaMaxima == diferenciaDentroRango){
                    medicion = generarCorrecta();
                }else if(diferenciaMaxima == diferenciaFueraRango){
                    medicion = generarFueraDeRango();
                }else if(diferenciaMaxima == diferenciaErroneo){
                    medicion = generarErronea();
                }
            }
        }
        return medicion;
    }

    public double generarCorrecta(){
        Random random = new Random();
        return this.getLimiteInferior() + (this.getLimiteSuperior() - this.getLimiteInferior()) * random.nextDouble();
    }

    public double generarFueraDeRango(){
        Random random = new Random();
        double rangoFuera = Math.max(Math.abs(this.getLimiteInferior()), Math.abs(this.getLimiteSuperior()));
        boolean generarMenor = random.nextBoolean();
        if (generarMenor) {
            return this.getLimiteInferior() - random.nextDouble() * rangoFuera;
        } else {
            return this.getLimiteSuperior() + random.nextDouble() * rangoFuera;
        }
    }

    public double generarErronea(){
        Random random = new Random();
        return random.nextDouble() * -1;
    }

    public  boolean alerta(double medicion){
        return medicion < this.getLimiteInferior() || medicion > this.getLimiteSuperior();
    }
}