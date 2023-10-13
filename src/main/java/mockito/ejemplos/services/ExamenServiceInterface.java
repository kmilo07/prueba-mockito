package mockito.ejemplos.services;

import mockito.ejemplos.models.Examen;

public interface ExamenServiceInterface {
    Examen findExamentPorNombre(String nombre);
}
