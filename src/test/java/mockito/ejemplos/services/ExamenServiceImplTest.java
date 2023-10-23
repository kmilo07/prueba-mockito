package mockito.ejemplos.services;

import mockito.ejemplos.models.Examen;
import mockito.ejemplos.repositories.ExamenRepositoryInterface;
import mockito.ejemplos.repositories.PreguntaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

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
    @Test
    void testPreguntasExamenCualquiera() {
        when(repositoryInterface.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPreguntas("Lenguaje");
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("derivadas"));
    }

    /*
    Es verify es un metodo que nos ayuda a saber si un metodo se ejecuta o no, en este caso en el servicio tenemos un if
    si el if no cumple, no va a ejecutar el pregunta repository por lo cual debe de arrojar error
     */
    @Test
    void testPreguntasExamenVerify() {
        when(repositoryInterface.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPreguntas("Lenguaje");
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("derivadas"));
        verify(repositoryInterface).findAll();   //El punto debe de estar fuera de los parentesis
        verify(preguntaRepository).findPreguntasPorExamenId(6L); // debe ser el id del lenguaje sino falla
    }
    @Test
    void testNoExisteExamenVerify() {
        when(repositoryInterface.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPreguntas("Lenguaje2");
        assertNull(examen);
        verify(repositoryInterface).findAll();   //El punto debe de estar fuera de los parentesis
        verify(preguntaRepository).findPreguntasPorExamenId(6L);
    }
}
