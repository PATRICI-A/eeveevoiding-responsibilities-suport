package edu.eci.patricia.config;

import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.WellnessResourceEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.JpaWellnessResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Loads initial wellness resources on startup (PTR23).
 * Uses updated WellnessCategory values: HEALTH, SPORTS, CULTURE, EMOTIONAL_SUPPORT.
 * Only runs if the table is empty to avoid duplicates on restart.
 * Active only in local/seed profiles to avoid touching shared QA/Prod databases on startup.
 */
@Slf4j
@Component
@Profile({"local", "seed"})
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final JpaWellnessResourceRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            log.info("[DataLoader] BD ya contiene {} recursos. Se omite la carga.", repository.count());
            return;
        }

        List<WellnessResourceEntity> resources = List.of(

                // ── EMOTIONAL_SUPPORT ──────────────────────────────────────────────
                WellnessResourceEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Servicio de Psicología")
                        .description("Apoyo psicológico individual y grupal para estudiantes. " +
                                "Sesiones confidenciales de orientación emocional y seguimiento.")
                        .category(WellnessCategory.EMOTIONAL_SUPPORT)
                        .location("Bloque A, Piso 2, Oficina 201")
                        .contactInfo("601-668-3600 ext. 1234 | psicologia@escuelaing.edu.co")
                        .schedule("Lunes a viernes, 8:00 a.m. – 5:00 p.m.")
                        .available(true)
                        .appointmentEmail("psicologia@escuelaing.edu.co")
                        .psychologistName("Dra. Andrea Gómez")
                        .build(),

                WellnessResourceEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Grupo de Apoyo Emocional")
                        .description("Sesiones grupales semanales para estudiantes que desean " +
                                "compartir experiencias y apoyarse mutuamente.")
                        .category(WellnessCategory.EMOTIONAL_SUPPORT)
                        .location("Sala de Reuniones, Bloque B")
                        .contactInfo("bienestar@escuelaing.edu.co")
                        .schedule("Miércoles, 4:00 p.m. – 6:00 p.m.")
                        .available(true)
                        .build(),

                // ── HEALTH ────────────────────────────────────────────────────────
                WellnessResourceEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Enfermería y Medicina General")
                        .description("Atención médica básica, primeros auxilios y seguimiento " +
                                "de condiciones de salud para estudiantes.")
                        .category(WellnessCategory.HEALTH)
                        .location("Bloque C, Planta Baja")
                        .contactInfo("601-668-3600 ext. 1100 | salud@escuelaing.edu.co")
                        .schedule("Lunes a viernes, 7:00 a.m. – 6:00 p.m.")
                        .available(true)
                        .build(),

                WellnessResourceEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Servicio de Nutrición")
                        .description("Asesoría nutricional personalizada, planes de alimentación " +
                                "saludable y talleres de hábitos alimenticios.")
                        .category(WellnessCategory.HEALTH)
                        .location("Bloque A, Piso 1")
                        .contactInfo("nutricion@escuelaing.edu.co")
                        .schedule("Lunes, miércoles y viernes, 9:00 a.m. – 1:00 p.m.")
                        .available(true)
                        .build(),

                // ── SPORTS ────────────────────────────────────────────────────────
                WellnessResourceEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Canchas de Fútbol")
                        .description("Canchas de fútbol 5 para uso libre o reserva. " +
                                "Disponibles para toda la comunidad estudiantil.")
                        .category(WellnessCategory.SPORTS)
                        .location("Zona Deportiva, Cancha 1 y 2")
                        .contactInfo("601-668-3600 ext. 2001 | deportes@escuelaing.edu.co")
                        .schedule("Lunes a sábado, 6:00 a.m. – 10:00 p.m.")
                        .available(true)
                        .build(),

                WellnessResourceEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Gimnasio Institucional")
                        .description("Gimnasio completamente equipado con máquinas cardiovasculares, " +
                                "zona de pesas y áreas de estiramiento.")
                        .category(WellnessCategory.SPORTS)
                        .location("Zona Deportiva, Edificio de Bienestar")
                        .contactInfo("601-668-3600 ext. 2002")
                        .schedule("Lunes a viernes, 6:00 a.m. – 9:00 p.m.")
                        .available(true)
                        .build(),

                WellnessResourceEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Piscina")
                        .description("Piscina semi-olímpica para natación recreativa y de competencia. " +
                                "Requiere inscripción previa en el semestre.")
                        .category(WellnessCategory.SPORTS)
                        .location("Zona Deportiva, Piscina Cubierta")
                        .contactInfo("601-668-3600 ext. 2003")
                        .schedule("Lunes a viernes, 7:00 a.m. – 8:00 p.m.")
                        .available(true)
                        .build(),

                // ── CULTURE ───────────────────────────────────────────────────────
                WellnessResourceEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Grupo de Teatro ECI")
                        .description("Grupo estudiantil de teatro abierto a toda la comunidad. " +
                                "Ensayos semanales y presentaciones cada semestre.")
                        .category(WellnessCategory.CULTURE)
                        .location("Auditorio Principal, Piso 1")
                        .contactInfo("cultura@escuelaing.edu.co")
                        .schedule("Martes y jueves, 5:00 p.m. – 7:00 p.m.")
                        .available(true)
                        .build(),

                WellnessResourceEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Banda Musical Institucional")
                        .description("Agrupación musical institucional abierta a estudiantes " +
                                "con conocimientos musicales previos.")
                        .category(WellnessCategory.CULTURE)
                        .location("Sala de Música, Bloque B")
                        .contactInfo("cultura@escuelaing.edu.co")
                        .schedule("Miércoles, 4:00 p.m. – 7:00 p.m.")
                        .available(true)
                        .build(),

                WellnessResourceEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Talleres de Arte y Pintura")
                        .description("Talleres creativos de arte, pintura y manualidades para " +
                                "estudiantes de todos los programas.")
                        .category(WellnessCategory.CULTURE)
                        .location("Taller de Arte, Bloque C")
                        .contactInfo("cultura@escuelaing.edu.co")
                        .schedule("Viernes, 3:00 p.m. – 6:00 p.m.")
                        .available(true)
                        .build()
        );

        repository.saveAll(resources);
        log.info("[DataLoader] {} recursos de bienestar cargados exitosamente.", resources.size());
    }
}
