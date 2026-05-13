package ec.edu.ups.academic;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Asignatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String ciclo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Profesor profesor;

    @ManyToMany
    @JoinTable(
            name = "asignatura_estudiante",
            joinColumns = @JoinColumn(name = "asignatura_id"),
            inverseJoinColumns = @JoinColumn(name = "estudiante_id")
    )
    private Set<Estudiante> estudiantes = new LinkedHashSet<>();

    protected Asignatura() {
    }

    public Asignatura(String nombre, String ciclo, Profesor profesor) {
        this.nombre = nombre;
        this.ciclo = ciclo;
        this.profesor = profesor;
    }

    public void addEstudiante(Estudiante estudiante) {
        estudiantes.add(estudiante);
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCiclo() {
        return ciclo;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public Set<Estudiante> getEstudiantes() {
        return estudiantes;
    }
}
