package org.example.Edge;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.example.utils.TipoSensor;
import zmq.socket.Pair;

public class SensorTemperatura extends Sensor{

    public SensorTemperatura(Integer id, String tipoSensor, String archivoConfig) {
        super(id, tipoSensor, archivoConfig);
        this.setIntervalo(3);
        this.setLimiteInferior(TipoSensor.TEMPERATURA_INFERIOR);
        this.setLimiteSuperior(TipoSensor.TEMPERATURA_SUPERIOR);
    }

}