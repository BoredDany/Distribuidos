package org.example.utils;

import com.google.gson.Gson;

public class Checkeo {
    private String ip;
    private boolean works;

    public Checkeo(String ip, boolean works) {
        this.ip = ip;
        this.works = works;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setWorks(boolean works) {
        this.works = works;
    }

    public boolean isWorks() {
        return works;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public String toString() {
        return "Checkeo{" +
                "ip='" + ip + '\'' +
                ", works=" + works +
                '}';
    }

    public static Checkeo fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Checkeo.class);
    }
}
