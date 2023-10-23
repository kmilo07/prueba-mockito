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

        when(repositoryInterface.findAll()).thenReturn(Datos.EXAMENES);
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

    @Test
    void testPreguntasExamen() {
        when(repositoryInterface.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(6L)).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPreguntas("Lenguaje");
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("derivadas"));
    }
}
