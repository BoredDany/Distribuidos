package org.example.Edge;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import zmq.socket.Pair;

public class SensorTemperatura extends Sensor{

    public SensorTemperatura(String tipoSensor, String archivoConfig) {
        super(tipoSensor, archivoConfig);
        this.setIntervalo(6);
        this.setLimiteInferior(11);
        this.setLimiteSuperior(29,4);
    }

    @Override
    public double generarMedicion(List<Double> dentroRango,List<Double> fueraRango, List<Double> erroreno) {
        //si todo esta en cero generar una correcta
        //calcular porcentajes
        //si las proporciones se mantienen generar una correcta
        //si no ver cual proporción se incumple
        Random random = new Random();
        double randomValue = 200.0 * random.nextDouble() - 100.0;
        return Math.round(randomValue * 10.0) / 10.0;
    }

}