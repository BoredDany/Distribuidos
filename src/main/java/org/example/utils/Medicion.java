package org.example.utils;

import com.google.gson.Gson;

public class Medicion {
    private String tipoSensor;
    private Integer idSensor;
    private Double medicion;
    private String hora;
    private boolean alerta;
    private boolean correcta;

    public Medicion(String tipoSensor, Integer idSensor, Double medicion, String hora, boolean alerta, boolean correcta) {
        this.tipoSensor = tipoSensor;
        this.idSensor = idSensor;
        this.medicion = medicion;
        this.hora = hora;
        this.alerta = alerta;
        this.correcta = correcta;
    }

    public String medicionStr(){
        return "Sensor " + this.idSensor + " de "
                + this.tipoSensor + ":" + this.medicion
                + " a las " + this.hora + " con alerta: "
                + this.alerta + " con error: "
                + this.correcta;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Medicion fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Medicion.class);
    }
}
