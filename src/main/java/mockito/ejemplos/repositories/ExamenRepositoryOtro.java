package mockito.ejemplos.repositories;

import mockito.ejemplos.models.Examen;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamenRepositoryOtro implements ExamenRepositoryInterface{
    @Override
    public List<Examen> findAll() {
        try{
            System.out.println("ExamenRepositoryOtro");
            TimeUnit.SECONDS.sleep(5);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        return null;
    }
}
