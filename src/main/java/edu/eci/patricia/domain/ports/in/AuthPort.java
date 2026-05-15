package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.domain.model.AuthUser;

public interface AuthPort {
    AuthUser validateToken(String token);
}