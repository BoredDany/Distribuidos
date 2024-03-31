package org.example.Edge;
import java.util.Random;
import zmq.socket.Pair;

public class SensorHumo extends Sensor {

    public SensorHumo() {
    }

    public SensorHumo(String tipoSensor, String archivoConfig, double limiteInferior, double LimiteSuperior) {
        super(tipoSensor, archivoConfig, limiteInferior, LimiteSuperior);
    }

    @Override
    public void inicializar(String tipo, String archivoConfig, double limiteInferior, double limiteSuperior) {
        super.inicializar(tipo, archivoConfig, limiteInferior, limiteSuperior);
        tipo = "Humo";
        archivoConfig = "configHumo.txt";
        limiteInferior = 0.0;
        limiteSuperior = 1.0;
    }

    @Override
    public double generarMedicion() {
        Random random = new Random();
        return random.nextInt(2);
    }

}