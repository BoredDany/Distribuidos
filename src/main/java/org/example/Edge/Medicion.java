package org.example.Edge;

import java.time.LocalTime;

public class Medicion {
    private String tipoSensor;
    private Integer pidSensor;
    private Double medicion;
    private LocalTime hora;
    private boolean alerta;

    public Medicion(String tipoSensor, Integer pidSensor, Double medicion, LocalTime hora, boolean alerta) {
        this.tipoSensor = tipoSensor;
        this.pidSensor = pidSensor;
        this.medicion = medicion;
        this.hora = hora;
        this.alerta = alerta;
    }
}
