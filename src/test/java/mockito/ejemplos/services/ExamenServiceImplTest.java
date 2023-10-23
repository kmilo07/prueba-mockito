package mockito.ejemplos.services;

import mockito.ejemplos.models.Examen;
import mockito.ejemplos.repositories.ExamenRepositoryInterface;
import mockito.ejemplos.repositories.PreguntaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ExamenServiceImplTest {

    ExamenRepositoryInterface repositoryInterface;
    ExamenServiceInterface service;
    PreguntaRepository preguntaRepository;

    @BeforeEach
    void setUp() {
        repositoryInterface = mock(ExamenRepositoryInterface.class);
        preguntaRepository = mock(PreguntaRepository.class);
        service = new ExamenServiceImpl(repositoryInterface, preguntaRepository);
    }

    @Test
    void findExamenPorNombre() {
        List<Examen> datos = Arrays.asList(new Examen(5L,"Matemáticas"), new Examen(6L,"Lenguaje"),
                new Examen(7L,"Historia"));

        when(repositoryInterface.findAll()).thenReturn(datos);
        Optional<Examen> examen = service.findExamentPorNombre("Matemáticas");

        assertTrue(examen.isPresent());
        assertEquals(5L,examen.orElseThrow().getId());
        assertEquals("Matemáticas",examen.orElseThrow().getNombre());
    }

    @Test
    void findExamenPorNombreListaVacia() {
        List<Examen> datos = Collections.emptyList();

        when(repositoryInterface.findAll()).thenReturn(datos);
        Optional<Examen> examen = service.findExamentPorNombre("Matemáticas");

        assertFalse(examen.isPresent());
    }



}
