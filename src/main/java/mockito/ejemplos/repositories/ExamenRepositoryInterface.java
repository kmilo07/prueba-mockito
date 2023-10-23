package mockito.ejemplos.repositories;

import mockito.ejemplos.models.Examen;

import java.util.List;

public interface ExamenRepositoryInterface {
    List<Examen> findAll();
}
