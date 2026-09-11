package com.juandiego.sistema_tickets_soporte.controller;

import com.juandiego.sistema_tickets_soporte.dto.UsuarioRegistroDTO;
import com.juandiego.sistema_tickets_soporte.dto.UsuarioResponseDTO;
import com.juandiego.sistema_tickets_soporte.model.Usuario;
import com.juandiego.sistema_tickets_soporte.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody UsuarioRegistroDTO dto) {
        Usuario usuario = Usuario.builder()
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .rol(Usuario.Rol.valueOf(dto.getRol().toUpperCase()))
                .build();

        Usuario creado = usuarioService.registrar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponseDTO.desde(creado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(UsuarioResponseDTO.desde(usuario));
    }
}