package org.example.Edge;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import zmq.socket.Pair;

public class SensorTemperatura extends Sensor{
    public SensorTemperatura(String tipoSensor, String archivoConfig) {
        super(tipoSensor, archivoConfig);
        this.setIntervalo(6);
    }

    @Override
    public double generarMedicion() {
        Random random = new Random();
        double randomValue = 200.0 * random.nextDouble() - 100.0;
        return Math.round(randomValue * 10.0) / 10.0;
    }

}