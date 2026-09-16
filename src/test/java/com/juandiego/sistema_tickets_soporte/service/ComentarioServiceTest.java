package com.juandiego.sistema_tickets_soporte.service;

import com.juandiego.sistema_tickets_soporte.model.Comentario;
import com.juandiego.sistema_tickets_soporte.model.Ticket;
import com.juandiego.sistema_tickets_soporte.model.Usuario;
import com.juandiego.sistema_tickets_soporte.repository.ComentarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComentarioServiceTest {

    @Mock
    private ComentarioRepository comentarioRepository;

    @InjectMocks
    private ComentarioService comentarioService;

    private Usuario autor;
    private Ticket ticket;
    private Comentario comentario;

    @BeforeEach
    void setUp() {
        autor = Usuario.builder()
                .id(1L)
                .nombre("Juan Diego")
                .email("juan@test.com")
                .rol(Usuario.Rol.CLIENTE)
                .build();

        ticket = Ticket.builder()
                .id(1L)
                .titulo("No puedo iniciar sesión")
                .cliente(autor)
                .build();

        comentario = Comentario.builder()
                .id(1L)
                .contenido("Estamos revisando el problema")
                .ticket(ticket)
                .autor(autor)
                .build();
    }

    @Test
    @DisplayName("Debe agregar un comentario correctamente")
    void agregar_debeGuardarComentarioConTicketYAutorCorrectos() {
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentario);

        Comentario resultado = comentarioService.agregar(ticket, autor, "Estamos revisando el problema");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContenido()).isEqualTo("Estamos revisando el problema");
        verify(comentarioRepository, times(1)).save(any(Comentario.class));
    }

    @Test
    @DisplayName("Al agregar un comentario, debe construirlo con el ticket y autor recibidos")
    void agregar_debeAsociarTicketYAutorAlComentario() {
        ArgumentCaptor<Comentario> captor = ArgumentCaptor.forClass(Comentario.class);
        when(comentarioRepository.save(captor.capture())).thenReturn(comentario);

        comentarioService.agregar(ticket, autor, "Contenido de prueba");

        Comentario comentarioCapturado = captor.getValue();
        assertThat(comentarioCapturado.getTicket()).isEqualTo(ticket);
        assertThat(comentarioCapturado.getAutor()).isEqualTo(autor);
        assertThat(comentarioCapturado.getContenido()).isEqualTo("Contenido de prueba");
    }

    @Test
    @DisplayName("Debe listar comentarios de un ticket ordenados cronológicamente")
    void listarPorTicket_debeRetornarComentariosDelTicket() {
        when(comentarioRepository.findByTicketOrderByFechaCreacionAsc(ticket))
                .thenReturn(List.of(comentario));

        List<Comentario> resultado = comentarioService.listarPorTicket(ticket);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getContenido()).isEqualTo("Estamos revisando el problema");
        verify(comentarioRepository, times(1)).findByTicketOrderByFechaCreacionAsc(ticket);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando el ticket no tiene comentarios")
    void listarPorTicket_sinComentarios_debeRetornarListaVacia() {
        when(comentarioRepository.findByTicketOrderByFechaCreacionAsc(ticket))
                .thenReturn(List.of());

        List<Comentario> resultado = comentarioService.listarPorTicket(ticket);

        assertThat(resultado).isEmpty();
    }
}