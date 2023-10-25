package mockito.ejemplos.services;

import mockito.ejemplos.models.Examen;
import mockito.ejemplos.repositories.ExamenRepositoryInterface;
import mockito.ejemplos.repositories.PreguntaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) // Otra forma de habilitar la anotacion de injeccion, se debe tener el jupiter
class ExamenServiceImplTest {

    @Mock
    ExamenRepositoryInterface repositoryInterface;
    @Mock
    PreguntaRepository preguntaRepository;

    @InjectMocks
    ExamenServiceImpl service; //No puede ser una interfaz si no la clase concreta

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.openMocks(this); //Se necesita habilitar la anotacion de injeccion
//        repositoryInterface = mock(ExamenRepositoryInterface.class);
//        preguntaRepository = mock(PreguntaRepository.class);
//        service = new ExamenServiceImpl(repositoryInterface, preguntaRepository);
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
        Examen examen = service.findExamenPorNombreConPreguntas("Lenguaje");
        assertNotNull(examen);
        verify(repositoryInterface).findAll();   //El punto debe de estar fuera de los parentesis
        verify(preguntaRepository).findPreguntasPorExamenId(6L);
    }

    @Test
    void testGuardarExamen() {
        when(repositoryInterface.guardar(any(Examen.class))).thenReturn(Datos.EXAMEN);
        Examen examen = service.guardar(Datos.EXAMEN);
        assertEquals(8L, examen.getId());
        assertEquals("Fisica",examen.getNombre());

        verify(repositoryInterface).guardar(any(Examen.class));
    }

    @Test
    void testGuardarExamenConPreguntas() {
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);
        when(repositoryInterface.guardar(any(Examen.class))).thenReturn(Datos.EXAMEN);
        Examen examen = service.guardar(newExamen);
        assertEquals(8L, examen.getId());
        assertEquals("Fisica",examen.getNombre());

        verify(repositoryInterface).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());
    }
    @Test
    void testGuardarExamenWithoutId() {
        //Given precondiciones para entorno de pruebas
        Examen newExamen = Datos.EXAMEN2;
        newExamen.setPreguntas(Datos.PREGUNTAS);
        when(repositoryInterface.guardar(any(Examen.class))).then(new Answer<Examen>() {

            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocation) throws Throwable{
                Examen examen  = invocation.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        });

        //When
        Examen examen = service.guardar(newExamen);
        assertEquals(8L, examen.getId());
        assertEquals("Fisica",examen.getNombre());

        //Then
        verify(repositoryInterface).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());
    }

    @Test
    void testManejoException() {
        when(repositoryInterface.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenThrow(IllegalArgumentException.class);
        Exception exception = assertThrows(IllegalArgumentException.class,()-> service.findExamenPorNombreConPreguntas("Lenguaje"));
        assertEquals(IllegalArgumentException.class, exception.getClass());

        verify(repositoryInterface).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }
    @Test
    void testManejoExceptionIsNull() {
        when(repositoryInterface.findAll()).thenReturn(Datos.EXAMENES_NULL);
        when(preguntaRepository.findPreguntasPorExamenId(isNull())).thenThrow(IllegalArgumentException.class);
        Exception exception = assertThrows(IllegalArgumentException.class,()-> service.findExamenPorNombreConPreguntas("Lenguaje"));
        assertEquals(IllegalArgumentException.class, exception.getClass());

        verify(repositoryInterface).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(isNull());
    }

    @Test
    void testArgumentMatcher(){
        when(repositoryInterface.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        service.findExamenPorNombreConPreguntas("Lenguaje");

        verify(repositoryInterface).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg-> arg.equals(6L)));
        verify(preguntaRepository).findPreguntasPorExamenId(eq(6L));
    }

    @Test
    void testArgumentMatcherCustomisado(){
        //when(repositoryInterface.findAll()).thenReturn(Datos.EXAMENES_NEGATIVE);
        when(repositoryInterface.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        service.findExamenPorNombreConPreguntas("Lenguaje");

        verify(repositoryInterface).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(new MiArgMatchers()));
    }

    public static class MiArgMatchers implements ArgumentMatcher<Long>{
        private Long argument;

        @Override
        public boolean matches(Long argument) {
            this.argument = argument;
            return argument != null && argument > 0;
        }

        @Override
        public String toString() {
            return "es para un mensaje cuando falla; "+argument+" debe ser un numero entero positivo.";
        }
    }
}
