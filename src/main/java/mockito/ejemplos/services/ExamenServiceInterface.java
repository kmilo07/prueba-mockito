package mockito.ejemplos.services;

import mockito.ejemplos.models.Examen;

import java.util.Optional;

public interface ExamenServiceInterface {
    Optional<Examen> findExamentPorNombre(String nombre);
}
