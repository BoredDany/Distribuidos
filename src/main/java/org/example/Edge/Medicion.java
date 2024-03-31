package org.example.Edge;

import java.time.LocalTime;

public class Medicion {
    private String tipoSensor;
    private Integer idSensor;
    private Double medicion;
    private LocalTime hora;
    private boolean alerta;

    public Medicion(String tipoSensor, Integer idSensor, Double medicion, LocalTime hora, boolean alerta) {
        this.tipoSensor = tipoSensor;
        this.idSensor = idSensor;
        this.medicion = medicion;
        this.hora = hora;
        this.alerta = alerta;
    }
}
