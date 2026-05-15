package edu.eci.patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.domain.exceptions.UnauthorizedException;
import edu.eci.patricia.domain.model.AuthUser;
import edu.eci.patricia.domain.ports.in.AuthPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class AuthMockAdapter implements AuthPort {

    @Override
    public AuthUser validateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Token not provided");
        }
        return new AuthUser("user-mock-123", "ADMIN");
    }
}