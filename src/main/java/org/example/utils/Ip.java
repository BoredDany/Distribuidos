package org.example.utils;

public class Ip {

    //cloud y fog misma maquina SANTI
    //EDGE EN LA DE DANI
    //FOG SECUNDARIO EN LA DE NICO

    public static final String nico = "10.43.100.243";
    public static final String dani = "10.43.100.223";
    public static final String santi = "10.43.101.18";

    //EDGE
    public static final String IP_EDGE = dani;


    //FOG
    public static String IP_FOG = dani;
    //CLOUD: EN ESTE CASO HEALTHCHECKER CORRERA EN LA MISMA MAQUINA DE CLOUD
    public static final String IP_CLOUD = dani;

    //FOG SECUNDARIO
    public static String IP_FOG_SECUNDARIO = dani;

    //PORTS
    public static final String PORT_SENSOR_ASPERSOR = "5000";
    public static final String PORT_SC_EDGE = "5100";
    public static final String PORT_SENSOR_PROXY = "5555";

    public static final String PORT_PROXY_CLOUD = "5500";
    public static final String PORT_PROXY_CHECKER = "5556";

    //PORTS CHECKER
    public static final String PORT_CLOUD_CHECKER = "5400";
    public static final String PORT_EDGE_CHECKER_HUMO = "5600";
    public static final String PORT_EDGE_CHECKER_HUMEDAD = "5700";
    public static final String PORT_EDGE_CHECKER_TEMPERATURA = "5800";

    public static final String PORT_SC_FOG = "5200";
    public static final String PORT_SC_CLOUD = "5300";

}

