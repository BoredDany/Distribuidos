package org.example.Edge;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import zmq.socket.Pair;

public class SensorTemperatura extends Sensor{


    public SensorTemperatura() {
    }

    public SensorTemperatura(String tipoSensor, String archivoConfig, double limiteInferior, double LimiteSuperior) {
        super(tipoSensor, archivoConfig, limiteInferior, LimiteSuperior);
    }

    @Override
    public void inicializar(String tipo, String archivoConfig, double limiteInferior, double limiteSuperior) {
        super.inicializar(tipo, archivoConfig, limiteInferior, limiteSuperior);
    }

    @Override
    public double generarMedicion() {
        Random random = new Random();
        double randomValue = 200.0 * random.nextDouble() - 100.0;
        return Math.round(randomValue * 10.0) / 10.0;
    }


}