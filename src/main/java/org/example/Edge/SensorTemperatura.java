package org.example.Edge;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import zmq.socket.Pair;

public class SensorTemperatura extends Sensor{

    public SensorTemperatura(Integer id, String tipoSensor, String archivoConfig) {
        super(id, tipoSensor, archivoConfig);
        this.setIntervalo(3);
        this.setLimiteInferior(11);
        this.setLimiteSuperior(29.4);
    }

}