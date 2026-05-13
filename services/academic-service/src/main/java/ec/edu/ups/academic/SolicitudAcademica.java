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
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class SolicitudAcademica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String tema;

    @Column(nullable = false)
    private Instant fechaCreacion;

    @Column(nullable = false, length = 40)
    private String estado;

    @Column(nullable = false, columnDefinition = "text")
    private String proveedoresUsados;

    @Column(nullable = false, columnDefinition = "text")
    private String resumenResultado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Profesor profesor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Asignatura asignatura;

    @ManyToMany
    @JoinTable(
            name = "solicitud_estudiante",
            joinColumns = @JoinColumn(name = "solicitud_id"),
            inverseJoinColumns = @JoinColumn(name = "estudiante_id")
    )
    private Set<Estudiante> estudiantes = new LinkedHashSet<>();

    protected SolicitudAcademica() {
    }

    public SolicitudAcademica(
            String tema,
            String estado,
            String proveedoresUsados,
            String resumenResultado,
            Profesor profesor,
            Asignatura asignatura,
            Set<Estudiante> estudiantes
    ) {
        this.tema = tema;
        this.fechaCreacion = Instant.now();
        this.estado = estado;
        this.proveedoresUsados = proveedoresUsados;
        this.resumenResultado = resumenResultado;
        this.profesor = profesor;
        this.asignatura = asignatura;
        this.estudiantes = estudiantes;
    }

    public Long getId() {
        return id;
    }

    public String getTema() {
        return tema;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public String getEstado() {
        return estado;
    }

    public String getProveedoresUsados() {
        return proveedoresUsados;
    }

    public String getResumenResultado() {
        return resumenResultado;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public Set<Estudiante> getEstudiantes() {
        return estudiantes;
    }
}
