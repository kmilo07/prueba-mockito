package mockito.ejemplos.repositories;

import mockito.ejemplos.models.Examen;

import java.util.List;

public interface ExamenRepository {
    List<Examen> findAll();
}
