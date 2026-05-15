package edu.eci.patricia.application.dto.response;

import lombok.Builder;

@Builder
public record MailtoResponse(
        String emailTo,
        String subject,
        String body,
        String mailtoUri
) {}