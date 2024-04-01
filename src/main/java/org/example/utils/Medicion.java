package org.example.utils;

public class Medicion {
    private String tipoSensor;
    private Integer idSensor;
    private Double medicion;
    private String hora;
    private boolean alerta;

    public Medicion(String tipoSensor, Integer idSensor, Double medicion, String hora, boolean alerta) {
        this.tipoSensor = tipoSensor;
        this.idSensor = idSensor;
        this.medicion = medicion;
        this.hora = hora;
        this.alerta = alerta;
    }

    public String medicionStr(){
        return "Sensor " + this.idSensor + " de "
                + this.tipoSensor + ":" + this.medicion
                + " a las " + this.hora + " con alerta: "
                + this.alerta;
    }
}
