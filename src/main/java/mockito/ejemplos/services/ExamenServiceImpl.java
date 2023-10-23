package mockito.ejemplos.services;

import mockito.ejemplos.models.Examen;
import mockito.ejemplos.repositories.ExamenRepositoryInterface;

import java.util.Optional;

public class ExamenServiceImpl implements ExamenServiceInterface {
    private final ExamenRepositoryInterface repository;

    public ExamenServiceImpl(ExamenRepositoryInterface repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Examen> findExamentPorNombre(String nombre) {
        return  repository.findAll().stream()
                .filter(e -> e.getNombre().contains(nombre)).findFirst();
    }
}
