package org.example.Edge;
import java.util.Random;
import zmq.socket.Pair;

public class SensorHumo extends Sensor {
    public SensorHumo(String tipoSensor, String archivoConfig) {
        super(tipoSensor, archivoConfig);
        this.setIntervalo(3);
    }

    @Override
    public double generarMedicion(Integer dentroRango, Integer fueraRango, Integer erroreno) {
        Random random = new Random();
        return random.nextInt(2);
    }

    @Override
    public double generarCorrecta(){
        Random random = new Random();
        return random.nextInt(2);
    }

    @Override
    public double generarFueraDeRango(){
        Random random = new Random();
        double rangoFuera = Math.max(Math.abs(this.getLimiteInferior()), Math.abs(this.getLimiteSuperior()));
        boolean generarMenor = random.nextBoolean();
        if (generarMenor) {
            return this.getLimiteInferior() - random.nextDouble() * rangoFuera;
        } else {
            return this.getLimiteSuperior() + random.nextDouble() * rangoFuera;
        }
    }


    @Override
    public double generarErronea(){
        Random random = new Random();
        return random.nextDouble() * -1;
    }

}