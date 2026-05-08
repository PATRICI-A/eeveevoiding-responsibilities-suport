package com.patricia.suport;

import edu.eci.patricia.infrastructure.security.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private static final String SECRET = "clave-secreta-para-tests-minimo-32-chars!!";
    private static final String WRONG_SECRET = "clave-equivocada-para-tests-32-chars!!!!!";

    private JwtTokenProvider provider;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider(SECRET);
        secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    private String buildToken(String userId, String userName, String rol, long expirationMs) {
        return Jwts.builder()
                .subject(userId)
                .claim("userName", userName)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    @Test
    void validateToken_tokenValido_retornaTrue() {
        String token = buildToken("user-1", "Laura González", "ESTUDIANTE", 60_000);
        assertThat(provider.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_tokenExpirado_retornaFalse() {
        String token = buildToken("user-1", "Laura González", "ESTUDIANTE", -1000);
        assertThat(provider.validateToken(token)).isFalse();
    }

    @Test
    void validateToken_tokenConFirmaIncorrecta_retornaFalse() {
        // Token firmado con clave distinta
        SecretKey wrongKey = Keys.hmacShaKeyFor(WRONG_SECRET.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("user-1")
                .claim("userName", "Laura González")
                .claim("rol", "ESTUDIANTE")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(wrongKey)
                .compact();

        assertThat(provider.validateToken(token)).isFalse();
    }

    @Test
    void validateToken_tokenMalformado_retornaFalse() {
        assertThat(provider.validateToken("esto.no.es.un.jwt")).isFalse();
    }

    @Test
    void getUserId_tokenValido_retornaSubject() {
        String token = buildToken("user-42", "Laura González", "ESTUDIANTE", 60_000);
        assertThat(provider.getUserId(token)).isEqualTo("user-42");
    }

    @Test
    void getUserName_tokenValido_retornaUserName() {
        String token = buildToken("user-42", "Laura González", "ESTUDIANTE", 60_000);
        assertThat(provider.getUserName(token)).isEqualTo("Laura González");
    }

    @Test
    void getRol_tokenValido_retornaRol() {
        String token = buildToken("user-42", "Laura González", "ESTUDIANTE", 60_000);
        assertThat(provider.getRol(token)).isEqualTo("ESTUDIANTE");
    }
}