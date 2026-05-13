package ec.edu.ups.academic;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProfesorRepository profesores;
    private final EstudianteRepository estudiantes;
    private final AsignaturaRepository asignaturas;

    public DataSeeder(
            ProfesorRepository profesores,
            EstudianteRepository estudiantes,
            AsignaturaRepository asignaturas
    ) {
        this.profesores = profesores;
        this.estudiantes = estudiantes;
        this.asignaturas = asignaturas;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (profesores.count() > 0) {
            return;
        }

        Profesor fanny = profesores.save(new Profesor("Fanny Gutama", "fanny.gutama@ups.edu.ec"));
        Profesor andres = profesores.save(new Profesor("Andres Loja", "andres.loja@ups.edu.ec"));

        Estudiante maria = estudiantes.save(new Estudiante("Maria Torres", "maria.torres@est.ups.edu.ec"));
        Estudiante carlos = estudiantes.save(new Estudiante("Carlos Perez", "carlos.perez@est.ups.edu.ec"));
        Estudiante daniela = estudiantes.save(new Estudiante("Daniela Mora", "daniela.mora@est.ups.edu.ec"));
        Estudiante diego = estudiantes.save(new Estudiante("Diego Cardenas", "diego.cardenas@est.ups.edu.ec"));

        Asignatura arquitectura = new Asignatura("Arquitectura de Software", "Septimo ciclo", fanny);
        arquitectura.addEstudiante(maria);
        arquitectura.addEstudiante(carlos);
        arquitectura.addEstudiante(daniela);
        asignaturas.save(arquitectura);

        Asignatura gerencia = new Asignatura("Gerencia Informatica", "Octavo ciclo", andres);
        gerencia.addEstudiante(carlos);
        gerencia.addEstudiante(daniela);
        gerencia.addEstudiante(diego);
        asignaturas.save(gerencia);
    }
}
