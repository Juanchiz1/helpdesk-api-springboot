package com.juandiego.sistema_tickets_soporte;

import com.juandiego.sistema_tickets_soporte.exception.EmailYaExisteException;
import com.juandiego.sistema_tickets_soporte.exception.RecursoNoEncontradoException;
import com.juandiego.sistema_tickets_soporte.model.Usuario;
import com.juandiego.sistema_tickets_soporte.repository.UsuarioRepository;
import com.juandiego.sistema_tickets_soporte.service.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan Diego")
                .email("juan@test.com")
                .password("123456")
                .rol(Usuario.Rol.CLIENTE)
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("Debe registrar un usuario correctamente cuando el email no existe")
    void registrar_conEmailNuevo_debeGuardarUsuario() {
        when(usuarioRepository.existsByEmail(usuario.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("passwordEncriptado");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.registrar(usuario);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("juan@test.com");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el email ya existe")
    void registrar_conEmailExistente_debeLanzarExcepcion() {
        when(usuarioRepository.existsByEmail(usuario.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.registrar(usuario))
                .isInstanceOf(EmailYaExisteException.class)
                .hasMessageContaining(usuario.getEmail());

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe encriptar la contraseña antes de guardar")
    void registrar_debeEncriptarPassword() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hashBcryptSimulado");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.registrar(usuario);

        assertThat(resultado.getPassword()).isEqualTo("hashBcryptSimulado");
        verify(passwordEncoder, times(1)).encode("123456");
    }

    @Test
    @DisplayName("Debe encontrar un usuario existente por id")
    void buscarPorId_conIdExistente_debeRetornarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el id no existe")
    void buscarPorId_conIdInexistente_debeLanzarExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.buscarPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Debe encontrar un usuario existente por email")
    void buscarPorEmail_conEmailExistente_debeRetornarUsuario() {
        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.buscarPorEmail("juan@test.com");

        assertThat(resultado.getEmail()).isEqualTo("juan@test.com");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el email no existe")
    void buscarPorEmail_conEmailInexistente_debeLanzarExcepcion() {
        when(usuarioRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.buscarPorEmail("noexiste@test.com"))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}