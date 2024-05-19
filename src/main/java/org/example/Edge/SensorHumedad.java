package org.example.Edge;

import org.example.utils.TipoSensor;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class SensorHumedad extends Sensor{
    public SensorHumedad(Integer id, String tipoSensor, String archivoConfig) {
        super(id, tipoSensor, archivoConfig);
        this.setIntervalo(5);
        this.setLimiteInferior(TipoSensor.HUMEDAD_INFERIOR);
        this.setLimiteSuperior(TipoSensor.HUMEDAD_SUPERIOR);
    }
}