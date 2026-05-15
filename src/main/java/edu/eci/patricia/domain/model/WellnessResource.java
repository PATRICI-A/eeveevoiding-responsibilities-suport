package edu.eci.patricia.domain.model;

import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.domain.valueobjects.MailtoAppointment;
import edu.eci.patricia.domain.valueobjects.ResourceId;
import lombok.Getter;

import java.util.Objects;

@Getter
public class WellnessResource {

    private final ResourceId id;
    private String name;
    private String description;
    private String contact;
    private String schedule;
    private WellnessCategory category;
    private boolean active;
    private String psychologistName;
    private String appointmentEmail;

    public WellnessResource(
            ResourceId id,
            String name,
            String description,
            String contact,
            String schedule,
            WellnessCategory category,
            boolean active,
            String psychologistName,
            String appointmentEmail
    ) {
        this.id               = Objects.requireNonNull(id,          "id must not be null");
        this.name             = Objects.requireNonNull(name,        "name must not be null");
        this.description      = Objects.requireNonNull(description, "description must not be null");
        this.contact          = Objects.requireNonNull(contact,     "contact must not be null");
        this.schedule         = Objects.requireNonNull(schedule,    "schedule must not be null");
        this.category         = Objects.requireNonNull(category,    "category must not be null");
        this.active           = active;
        this.psychologistName = psychologistName;
        this.appointmentEmail = appointmentEmail;
    }

    public static WellnessResource createGeneral(
            String name,
            String description,
            String contact,
            String schedule,
            WellnessCategory category
    ) {
        return new WellnessResource(
                ResourceId.generate(),
                name, description, contact, schedule,
                category, true, null, null
        );
    }

    public static WellnessResource createMentalHealth(
            String name,
            String description,
            String contact,
            String schedule,
            String psychologistName,
            String appointmentEmail
    ) {
        Objects.requireNonNull(psychologistName, "psychologistName is required for MENTAL_HEALTH");
        Objects.requireNonNull(appointmentEmail, "appointmentEmail is required for MENTAL_HEALTH");

        return new WellnessResource(
                ResourceId.generate(),
                name, description, contact, schedule,
                WellnessCategory.MENTAL_HEALTH,
                true, psychologistName, appointmentEmail
        );
    }

    public boolean isMentalHealth() {
        return WellnessCategory.MENTAL_HEALTH.equals(this.category);
    }

    public MailtoAppointment buildMailtoAppointment(String studentName) {
        if (!isMentalHealth()) {
            throw new IllegalStateException("mailto appointments are only available for MENTAL_HEALTH resources");
        }
        Objects.requireNonNull(studentName, "studentName must not be null");

        String subject = "Appointment Request — " + studentName;
        String body    = "Dear " + psychologistName + ",\n\n"
                + "My name is " + studentName + " and I would like to schedule "
                + "a psychological support appointment at your earliest convenience.\n\n"
                + "Thank you.";

        return new MailtoAppointment(appointmentEmail, subject, body);
    }

    public void update(
            String name,
            String description,
            String contact,
            String schedule,
            String psychologistName,
            String appointmentEmail
    ) {
        this.name             = Objects.requireNonNull(name,        "name must not be null");
        this.description      = Objects.requireNonNull(description, "description must not be null");
        this.contact          = Objects.requireNonNull(contact,     "contact must not be null");
        this.schedule         = Objects.requireNonNull(schedule,    "schedule must not be null");
        this.psychologistName = psychologistName;
        this.appointmentEmail = appointmentEmail;
    }

    public void deactivate() {
        this.active = false;
    }
}