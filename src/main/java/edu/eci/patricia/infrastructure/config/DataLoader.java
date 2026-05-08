package edu.eci.patricia.infrastructure.config;

import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.WellnessResourceEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.JpaWellnessResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final JpaWellnessResourceRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            log.info("DataLoader: recursos ya cargados, omitiendo precarga.");
            return;
        }

        List<WellnessResourceEntity> resources = List.of(

                // MENTAL_HEALTH
                WellnessResourceEntity.builder()
                        .name("Servicio de Psicología ECI")
                        .description("Atención psicológica individual y grupal para estudiantes de la ECI. " +
                                "Apoyo en manejo de estrés, ansiedad, adaptación universitaria y bienestar emocional.")
                        .contactPhone("601-668-3600 ext. 101")
                        .contactEmail("psicologia@eci.edu.co")
                        .schedule("Lunes a viernes, 8:00 AM – 5:00 PM")
                        .category(WellnessCategory.MENTAL_HEALTH)
                        .active(true)
                        .appointmentEmail("citas.psicologia@eci.edu.co")
                        .psychologistName("Dra. María Fernanda López")
                        .build(),

                WellnessResourceEntity.builder()
                        .name("Grupo de Apoyo Emocional")
                        .description("Sesiones grupales semanales facilitadas por psicólogos de la institución. " +
                                "Espacio seguro para compartir experiencias y aprender estrategias de afrontamiento.")
                        .contactPhone("601-668-3600 ext. 102")
                        .contactEmail("grupoaopyo@eci.edu.co")
                        .schedule("Miércoles, 4:00 PM – 6:00 PM")
                        .category(WellnessCategory.MENTAL_HEALTH)
                        .active(true)
                        .appointmentEmail("grupos.bienestar@eci.edu.co")
                        .psychologistName("Dr. Andrés Camilo Pérez")
                        .build(),

                // SPORTS
                WellnessResourceEntity.builder()
                        .name("Gimnasio ECI")
                        .description("Gimnasio equipado con máquinas cardiovasculares, pesas libres y zona funcional " +
                                "disponible para todos los estudiantes con carné vigente.")
                        .contactPhone("601-668-3600 ext. 200")
                        .contactEmail("deportes@eci.edu.co")
                        .schedule("Lunes a viernes, 6:00 AM – 9:00 PM. Sábados, 7:00 AM – 2:00 PM")
                        .category(WellnessCategory.SPORTS)
                        .active(true)
                        .build(),

                WellnessResourceEntity.builder()
                        .name("Canchas Deportivas ECI")
                        .description("Canchas de fútbol, baloncesto y voleibol disponibles para reserva. " +
                                "Torneos intercarreras organizados cada semestre.")
                        .contactPhone("601-668-3600 ext. 201")
                        .contactEmail("canchas@eci.edu.co")
                        .schedule("Lunes a domingo, 7:00 AM – 8:00 PM")
                        .category(WellnessCategory.SPORTS)
                        .active(true)
                        .build(),

                // CULTURE
                WellnessResourceEntity.builder()
                        .name("Grupo de Teatro ECI")
                        .description("Taller de teatro abierto a todos los estudiantes. Presentaciones cada " +
                                "semestre en el auditorio principal. No se requiere experiencia previa.")
                        .contactPhone("601-668-3600 ext. 300")
                        .contactEmail("teatro@eci.edu.co")
                        .schedule("Jueves, 5:00 PM – 7:00 PM")
                        .category(WellnessCategory.CULTURE)
                        .active(true)
                        .build(),

                WellnessResourceEntity.builder()
                        .name("Banda Musical ECI")
                        .description("Agrupación musical universitaria que integra estudiantes de todos los programas. " +
                                "Participación en eventos institucionales y festivales universitarios.")
                        .contactPhone("601-668-3600 ext. 301")
                        .contactEmail("banda@eci.edu.co")
                        .schedule("Martes y jueves, 6:00 PM – 8:00 PM")
                        .category(WellnessCategory.CULTURE)
                        .active(true)
                        .build(),

                // ACADEMIC_SUPPORT
                WellnessResourceEntity.builder()
                        .name("Tutoría entre Pares")
                        .description("Programa de acompañamiento académico donde estudiantes de semestres " +
                                "avanzados apoyan a sus compañeros en materias con mayor dificultad.")
                        .contactPhone("601-668-3600 ext. 400")
                        .contactEmail("tutoria@eci.edu.co")
                        .schedule("Lunes a viernes, 9:00 AM – 6:00 PM. Citas previas recomendadas.")
                        .category(WellnessCategory.ACADEMIC_SUPPORT)
                        .active(true)
                        .build(),

                WellnessResourceEntity.builder()
                        .name("Centro de Escritura ECI")
                        .description("Asesoría personalizada para mejorar habilidades de redacción académica, " +
                                "citación APA/IEEE y estructura de trabajos de grado.")
                        .contactPhone("601-668-3600 ext. 401")
                        .contactEmail("escritura@eci.edu.co")
                        .schedule("Lunes a viernes, 8:00 AM – 5:00 PM")
                        .category(WellnessCategory.ACADEMIC_SUPPORT)
                        .active(true)
                        .build()
        );

        repository.saveAll(resources);
        log.info("DataLoader: {} recursos de bienestar cargados exitosamente.", resources.size());
    }
}