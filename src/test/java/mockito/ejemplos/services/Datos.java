package mockito.ejemplos.services;

import mockito.ejemplos.models.Examen;

import java.util.Arrays;
import java.util.List;

public class Datos{
    public final  static List<Examen> EXAMENES = Arrays.asList(new Examen(5L,"Matemáticas"), new Examen(6L,"Lenguaje"),
            new Examen(7L,"Historia"));
    public final  static List<Examen> EXAMENES_NULL = Arrays.asList(new Examen(null,"Matemáticas"), new Examen(null,"Lenguaje"),
            new Examen(null,"Historia"));

    public final static List<String> PREGUNTAS = Arrays.asList("aritmetica","integrales", "derivadas","trigonometria", "geometria");

    public final static Examen EXAMEN = new Examen(8L,"Fisica");
    public final static Examen EXAMEN2 = new Examen(null,"Fisica");
}
