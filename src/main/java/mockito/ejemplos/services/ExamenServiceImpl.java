package mockito.ejemplos.services;

import mockito.ejemplos.models.Examen;
import mockito.ejemplos.repositories.ExamenRepositoryInterface;
import mockito.ejemplos.repositories.PreguntaRepository;

import java.util.List;
import java.util.Optional;

public class ExamenServiceImpl implements ExamenServiceInterface {
    private final ExamenRepositoryInterface repository;
    private PreguntaRepository preguntaRepository;

    public ExamenServiceImpl(ExamenRepositoryInterface repository, PreguntaRepository preguntaRepository) {
        this.repository = repository;
        this.preguntaRepository = preguntaRepository;
    }

    @Override
    public Optional<Examen> findExamentPorNombre(String nombre) {
        return  repository.findAll().stream()
                .filter(e -> e.getNombre().contains(nombre)).findFirst();
    }

    @Override
    public Examen findExamenPorNombreConPreguntas(String nombre) {
        Optional<Examen> examenOptional = findExamentPorNombre(nombre);
        Examen examen = null;
        if(examenOptional.isPresent()){
            examen = examenOptional.orElseThrow();
            List<String> preguntas = preguntaRepository.findPreguntasPorExamenId(examenOptional.get().getId());
            examen.setPreguntas(preguntas);
        }
        return examen;
    }
}
