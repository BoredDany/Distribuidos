package org.example.Edge;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class SensorHumedad extends Sensor{
    public SensorHumedad(String tipoSensor, String archivoConfig) {
        super(tipoSensor, archivoConfig);
        this.setIntervalo(5);
    }

    @Override
    public double generarMedicion() {
        Random random = new Random();
        double randomValue = random.nextDouble() * 100;
        return Math.round(randomValue * 10.0) / 10.0;
    }

}