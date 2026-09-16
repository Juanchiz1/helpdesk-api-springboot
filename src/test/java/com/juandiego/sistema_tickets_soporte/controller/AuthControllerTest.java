package com.juandiego.sistema_tickets_soporte.controller;

import tools.jackson.databind.ObjectMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.juandiego.sistema_tickets_soporte.dto.LoginRequestDTO;
import com.juandiego.sistema_tickets_soporte.model.Usuario;
import com.juandiego.sistema_tickets_soporte.security.JwtAuthenticationFilter;
import com.juandiego.sistema_tickets_soporte.security.JwtService;
import com.juandiego.sistema_tickets_soporte.security.UsuarioDetails;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

   @MockitoBean
private AuthenticationManager authenticationManager;

   @MockitoBean
private JwtService jwtService;

    @MockitoBean
private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Usuario usuario;
    private UsuarioDetails usuarioDetails;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan Diego")
                .email("juan@test.com")
                .password("hashBcrypt")
                .rol(Usuario.Rol.CLIENTE)
                .activo(true)
                .build();

        usuarioDetails = new UsuarioDetails(usuario);
    }

    @Test
    @DisplayName("POST /api/auth/login con credenciales válidas debe retornar 200 y un token")
    void login_conCredencialesValidas_debeRetornarToken() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("juan@test.com");
        request.setPassword("123456");

        Authentication authMock = new UsernamePasswordAuthenticationToken(
                usuarioDetails, null, usuarioDetails.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authMock);
        when(jwtService.generarToken(usuarioDetails)).thenReturn("token.jwt.simulado");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token.jwt.simulado"))
                .andExpect(jsonPath("$.email").value("juan@test.com"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }

    @Test
    @DisplayName("POST /api/auth/login con credenciales inválidas debe retornar error")
    void login_conCredencialesInvalidas_debeRetornarError() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("juan@test.com");
        request.setPassword("passwordIncorrecto");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login sin email debe retornar 400 por validación")
    void login_sinEmail_debeRetornarBadRequest() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("");
        request.setPassword("123456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}