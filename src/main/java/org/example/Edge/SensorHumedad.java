package org.example.Edge;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class SensorHumedad extends Sensor{


    public SensorHumedad() {
    }

    public SensorHumedad(String tipoSensor, String archivoConfig, double limiteInferior, double LimiteSuperior) {
        super(tipoSensor, archivoConfig, limiteInferior, LimiteSuperior);
    }

    @Override
    public void inicializar(String tipo, String archivoConfig, double limiteInferior, double limiteSuperior) {
        super.inicializar(tipo, archivoConfig, limiteInferior, limiteSuperior);
        tipo = "Humedad";
        archivoConfig = archivoConfig;
        limiteInferior = 70.0;
        limiteSuperior = 100.0;
    }

    @Override
    public double generarMedicion() throws IOException {
        Random random = new Random();
        double randomValue = random.nextDouble() * 100;
        return Math.round(randomValue * 10.0) / 10.0;
    }

}
