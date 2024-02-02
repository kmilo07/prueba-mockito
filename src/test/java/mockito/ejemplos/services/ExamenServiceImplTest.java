package mockito.ejemplos.services;

import mockito.ejemplos.models.Examen;
import mockito.ejemplos.repositories.ExamenRepositoryInterface;
import mockito.ejemplos.repositories.PreguntaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) // Otra forma de habilitar la anotacion de injeccion, se debe tener el jupiter
class ExamenServiceImplTest {

    @Mock
    ExamenRepositoryInterface repositoryInterface;
    @Mock
    PreguntaRepository preguntaRepository;

    @InjectMocks
    ExamenServiceImpl service; //No puede ser una interfaz si no la clase concreta

    @Captor //otra forma de obtener los argumentos
    ArgumentCaptor<Long> captor;

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

    @Test
    void testArgumentCaptor() {
        when(repositoryInterface.findAll()).thenReturn(Datos.EXAMENES);

        service.findExamenPorNombreConPreguntas("Lenguaje");

        //ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class); 1ra forma
        verify(preguntaRepository).findPreguntasPorExamenId(captor.capture());

        assertEquals(6L, captor.getValue());

    }

    @Test
    void testDoThrow() {
        Examen examen = Datos.EXAMEN;
        examen.setPreguntas(Datos.PREGUNTAS);

        doThrow(IllegalArgumentException.class).when(preguntaRepository).guardarVarias(anyList());

        assertThrows(IllegalArgumentException.class,()->{
            service.guardar(examen);
        });
    }

    @Test
    void testDoAnswer() {
        when(repositoryInterface.findAll()).thenReturn(Datos.EXAMENES);
        doAnswer(invocation ->{
                Long id = invocation.getArgument(0);
                return id == 5L ? Datos.PREGUNTAS : Collections.emptyList();

                }).when(preguntaRepository).findPreguntasPorExamenId(anyLong());
        Examen examen = service.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5L, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());
        assertEquals(5,examen.getPreguntas().size());
    }

    @Test
    void testDoAnswerGuardarExamenWithoutId() {
        //Given precondiciones para entorno de pruebas
        Examen newExamen = Datos.EXAMEN2;
        newExamen.setPreguntas(Datos.PREGUNTAS);
        doAnswer(new Answer<Examen>() {

            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocation) throws Throwable{
                Examen examen  = invocation.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        }).when(repositoryInterface).guardar(any(Examen.class));

        //When
        Examen examen = service.guardar(newExamen);
        assertEquals(8L, examen.getId());
        assertEquals("Fisica",examen.getNombre());

        //Then
        verify(repositoryInterface).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());
    }

    /*
    el metodo doCall se utiliza para usar el metodo real
     */

    @Test
    void testDoCallRealMethod() {
        when(repositoryInterface.findAll()).thenReturn(Datos.EXAMENES);
//        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        doCallRealMethod().when(preguntaRepository).findPreguntasPorExamenId(anyLong());
        Examen examen = service.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5L, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());

    }

    @Test
    void testSpy() {
        ExamenRepository examenRepository = spy(ExamenRepositoryImpl.class);
        PreguntaRepository preguntaRepository = spy(PreguntaRepositoryImpl.class);
        ExamenService examenService = new ExamenServiceImpl(examenRepository, preguntaRepository);

        List<String> preguntas = Arrays.asList("aritmética");
//        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
        doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());
        assertEquals(1, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmética"));

        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

 /*
 para conocer el orden con el que se ejecutar una consulta
  */
 @Test
 void testOrdenDeInvocaciones2() {
     when(repository.findAll()).thenReturn(Datos.EXAMENES);

     service.findExamenPorNombreConPreguntas("Matemáticas");
     service.findExamenPorNombreConPreguntas("Lenguaje");

     InOrder inOrder = inOrder(repository, preguntaRepository);
     inOrder.verify(repository).findAll();
     inOrder.verify(preguntaRepository).findPreguntasPorExamenId(5L);

     inOrder.verify(repository).findAll();
     inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6L);

 }

 /*
  Test invocacion es el que se encarga de decir el
  */
 @Test
 void testNumeroDeInvocaciones() {
     when(repository.findAll()).thenReturn(Datos.EXAMENES);
     service.findExamenPorNombreConPreguntas("Matemáticas");

     verify(preguntaRepository).findPreguntasPorExamenId(5L);
     verify(preguntaRepository, times(1)).findPreguntasPorExamenId(5L);
     verify(preguntaRepository, atLeast(1)).findPreguntasPorExamenId(5L);
     verify(preguntaRepository, atLeastOnce()).findPreguntasPorExamenId(5L);
     verify(preguntaRepository, atMost(1)).findPreguntasPorExamenId(5L);
     verify(preguntaRepository, atMostOnce()).findPreguntasPorExamenId(5L);
 }

}
