package org.example.Cloud;

public class Cloud {
    private String ip;
    private String ipProxy;
    private String ipSistemaCalidad;
    private int intervaloHUmedad;

    public Cloud(String ip, String ipProxy, String ipSistemaCalidad, int intervaloHUmedad) {
        this.ip = ip;
        this.ipProxy = ipProxy;
        this.ipSistemaCalidad = ipSistemaCalidad;
        this.intervaloHUmedad = intervaloHUmedad;
    }

    public String getIp() {
        return ip;
    }

    public String getIpProxy() {
        return ipProxy;
    }

    public String getIpSistemaCalidad() {
        return ipSistemaCalidad;
    }

    public int getIntervaloHUmedad() {
        return intervaloHUmedad;
    }
}
