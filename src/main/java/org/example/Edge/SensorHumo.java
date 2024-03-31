package org.example.Edge;
import java.util.Random;
import zmq.socket.Pair;

public class SensorHumo extends Sensor {
    public SensorHumo(String tipoSensor, String archivoConfig) {
        super(tipoSensor, archivoConfig);
        this.setIntervalo(3);
    }

    @Override
    public double generarMedicion() {
        Random random = new Random();
        return random.nextInt(2);
    }

}