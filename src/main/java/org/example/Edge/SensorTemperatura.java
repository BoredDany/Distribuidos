package org.example.Edge;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import zmq.socket.Pair;

public class SensorTemperatura extends Sensor{

    public SensorTemperatura(String tipoSensor, String archivoConfig) {
        super(tipoSensor, archivoConfig);
        this.setIntervalo(3);
        this.setLimiteInferior(11);
        this.setLimiteSuperior(29,4);
    }

    @Override
    public double generarMedicion(Integer dentroRango,Integer fueraRango, Integer erroreno) {
        double medicion;

        //si están vacias retornar una correcta
        medicion = generarCorrecta();

        //si no calcular porcentajes
        double pDentroRango = (this.getProbDentroRango() / 100);
        double pFueraRango = (this.getProbFueraRango() / 100);
        double pErroneo = (this.getProbError() / 100);

        //calcular porcentajes
        Integer total = dentroRango + fueraRango + erroreno;
        double porcentajeDentroRango = (double) dentroRango / total;
        double porcentajeFueraRango = (double) fueraRango / total;
        double porcentajeErroneo = (double) erroreno / total;


        System.out.println("dentro" + porcentajeDentroRango +" -fuera:" + porcentajeFueraRango + "-err:" + porcentajeErroneo);
        //calcular diferencias
        double diferenciaDentroRango = pDentroRango - porcentajeDentroRango;
        double diferenciaFueraRango = pFueraRango - porcentajeFueraRango;
        double diferenciaErroneo = pErroneo - porcentajeErroneo;


        //si las proporciones se mantienen retornar una correcta
        if(pDentroRango == porcentajeDentroRango && pFueraRango == porcentajeFueraRango && pErroneo == porcentajeErroneo){
            medicion = generarCorrecta();
        }else{//si no ver cual proporción es menor a lo que debería y de esa generar un valor
            double diferenciaMaxima = Math.max(diferenciaDentroRango, Math.max(diferenciaFueraRango, diferenciaErroneo));
            if(diferenciaMaxima == diferenciaDentroRango){
                medicion = generarCorrecta();
            }else if(diferenciaMaxima == diferenciaFueraRango){
                medicion = generarFueraDeRango();
            }else if(diferenciaMaxima == diferenciaErroneo){
                medicion = generarErronea();
            }
        }
        return medicion;
    }

    @Override
    public double generarCorrecta(){
        Random random = new Random();
        return this.getLimiteInferior() + (this.getLimiteSuperior() - this.getLimiteInferior()) * random.nextDouble();
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