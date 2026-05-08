package com.patricia.suport;

import edu.eci.patricia.infrastructure.security.JwtAuthenticationFilter;
import edu.eci.patricia.infrastructure.security.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_tokenValido_cargaAutenticacionYContinua() throws Exception {
        String token = "valid.jwt.token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getUserId(token)).thenReturn("user-123");
        when(jwtTokenProvider.getUserName(token)).thenReturn("Laura González");
        when(jwtTokenProvider.getRol(token)).thenReturn("ESTUDIANTE");

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isEqualTo("user-123");
        assertThat(SecurityContextHolder.getContext().getAuthentication().getDetails())
                .isEqualTo("Laura González");
    }

    @Test
    void doFilter_sinToken_retorna401YNoSigueElFiltro() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void doFilter_tokenInvalido_retorna401YNoSigueElFiltro() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token.invalido");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenProvider.validateToken("token.invalido")).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void doFilter_headerSinBearerPrefix_retorna401() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void shouldNotFilter_swaggerPath_retornaTrue() throws Exception {
        MockHttpServletRequest swaggerRequest = new MockHttpServletRequest();
        swaggerRequest.setServletPath("/swagger-ui/index.html");
        Method shouldNotFilterMethod = JwtAuthenticationFilter.class.getDeclaredMethod("shouldNotFilter", jakarta.servlet.http.HttpServletRequest.class);
        shouldNotFilterMethod.setAccessible(true);
        boolean result = (boolean) shouldNotFilterMethod.invoke(filter, swaggerRequest);
        assertThat(result).isTrue();
    }

    @Test
    void shouldNotFilter_apiPath_retornaFalse() throws Exception {

        MockHttpServletRequest apiRequest = new MockHttpServletRequest();
        apiRequest.setServletPath("/api/v1/bienestar/recursos");

        Method shouldNotFilterMethod = JwtAuthenticationFilter.class.getDeclaredMethod("shouldNotFilter", jakarta.servlet.http.HttpServletRequest.class);
        shouldNotFilterMethod.setAccessible(true);

        boolean result = (boolean) shouldNotFilterMethod.invoke(filter, apiRequest);

        assertThat(result).isFalse();
    }
}