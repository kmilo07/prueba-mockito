package mockito.ejemplos.models;

import java.util.ArrayList;
import java.util.List;

public class Examen {
    private Long id;
    private String nombre;
    private List<String> preguntas;

    public Examen(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.preguntas = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public Examen setId(Long id) {
        this.id = id;
        return this;
    }

    public String getNombre() {
        return nombre;
    }

    public Examen setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public List<String> getPreguntas() {
        return preguntas;
    }

    public Examen setPreguntas(List<String> preguntas) {
        this.preguntas = preguntas;
        return this;
    }
}
