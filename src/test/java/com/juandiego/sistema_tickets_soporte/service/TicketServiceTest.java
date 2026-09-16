package com.juandiego.sistema_tickets_soporte.service;

import com.juandiego.sistema_tickets_soporte.exception.RecursoNoEncontradoException;
import com.juandiego.sistema_tickets_soporte.model.Ticket;
import com.juandiego.sistema_tickets_soporte.model.Usuario;
import com.juandiego.sistema_tickets_soporte.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    private Usuario cliente;
    private Usuario agente;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        cliente = Usuario.builder()
                .id(1L)
                .nombre("Juan Diego")
                .email("juan@test.com")
                .rol(Usuario.Rol.CLIENTE)
                .build();

        agente = Usuario.builder()
                .id(2L)
                .nombre("Ana Soporte")
                .email("ana@test.com")
                .rol(Usuario.Rol.AGENTE)
                .build();

        ticket = Ticket.builder()
                .id(1L)
                .titulo("No puedo iniciar sesión")
                .descripcion("Error 500 al hacer login")
                .estado(Ticket.Estado.ABIERTO)
                .prioridad(Ticket.Prioridad.ALTA)
                .cliente(cliente)
                .build();
    }

    @Test
    @DisplayName("Debe crear un ticket correctamente")
    void crear_debeGuardarTicket() {
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket resultado = ticketService.crear(ticket);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTitulo()).isEqualTo("No puedo iniciar sesión");
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    @DisplayName("Debe encontrar un ticket existente por id")
    void buscarPorId_conIdExistente_debeRetornarTicket() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        Ticket resultado = ticketService.buscarPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el ticket no existe")
    void buscarPorId_conIdInexistente_debeLanzarExcepcion() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.buscarPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Debe listar todos los tickets")
    void listarTodos_debeRetornarListaCompleta() {
        when(ticketRepository.findAll()).thenReturn(List.of(ticket));

        List<Ticket> resultado = ticketService.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTitulo()).isEqualTo("No puedo iniciar sesión");
    }

    @Test
    @DisplayName("Debe listar tickets filtrados por cliente")
    void listarPorCliente_debeRetornarTicketsDelCliente() {
        when(ticketRepository.findByCliente(cliente)).thenReturn(List.of(ticket));

        List<Ticket> resultado = ticketService.listarPorCliente(cliente);

        assertThat(resultado).hasSize(1);
        verify(ticketRepository, times(1)).findByCliente(cliente);
    }

    @Test
    @DisplayName("Debe listar tickets filtrados por agente")
    void listarPorAgente_debeRetornarTicketsDelAgente() {
        when(ticketRepository.findByAgenteAsignado(agente)).thenReturn(List.of(ticket));

        List<Ticket> resultado = ticketService.listarPorAgente(agente);

        assertThat(resultado).hasSize(1);
        verify(ticketRepository, times(1)).findByAgenteAsignado(agente);
    }

    @Test
    @DisplayName("Al asignar un agente, debe cambiar el estado a EN_PROGRESO")
    void asignarAgente_debeActualizarAgenteYEstado() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ticket resultado = ticketService.asignarAgente(1L, agente);

        assertThat(resultado.getAgenteAsignado()).isEqualTo(agente);
        assertThat(resultado.getEstado()).isEqualTo(Ticket.Estado.EN_PROGRESO);
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    @DisplayName("Al asignar agente a un ticket inexistente, debe lanzar excepción")
    void asignarAgente_conTicketInexistente_debeLanzarExcepcion() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.asignarAgente(99L, agente))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Debe cambiar el estado de un ticket")
    void cambiarEstado_debeActualizarEstado() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ticket resultado = ticketService.cambiarEstado(1L, Ticket.Estado.RESUELTO);

        assertThat(resultado.getEstado()).isEqualTo(Ticket.Estado.RESUELTO);
    }
}