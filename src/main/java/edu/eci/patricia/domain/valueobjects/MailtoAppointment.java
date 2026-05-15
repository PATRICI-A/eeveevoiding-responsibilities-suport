package edu.eci.patricia.domain.valueobjects;


public record MailtoAppointment(
        String emailTo,
        String subject,
        String body
) {

    public MailtoAppointment {
        if (emailTo == null || emailTo.isBlank()) throw new IllegalArgumentException("emailTo must not be blank");
        if (subject == null || subject.isBlank()) throw new IllegalArgumentException("subject must not be blank");
        if (body    == null || body.isBlank())    throw new IllegalArgumentException("body must not be blank");
    }

    public String toMailtoUri() {
        return String.format("mailto:%s?subject=%s&body=%s",
                emailTo,
                encodeComponent(subject),
                encodeComponent(body));
    }

    private String encodeComponent(String value) {
        return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8)
                .replace("+", "%20");
    }
}