package mockito.ejemplos.repositories;

import mockito.ejemplos.models.Examen;

import java.util.List;

public interface ExamenRepositoryInterface {
    Examen guardar(Examen examen);
    List<Examen> findAll();
}
