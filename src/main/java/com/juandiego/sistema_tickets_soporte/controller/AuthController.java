package com.juandiego.sistema_tickets_soporte.controller;

import com.juandiego.sistema_tickets_soporte.dto.LoginRequestDTO;
import com.juandiego.sistema_tickets_soporte.dto.LoginResponseDTO;
import com.juandiego.sistema_tickets_soporte.security.JwtService;
import com.juandiego.sistema_tickets_soporte.security.UsuarioDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        UsuarioDetails usuarioDetails = (UsuarioDetails) auth.getPrincipal();
        String token = jwtService.generarToken(usuarioDetails);

        return ResponseEntity.ok(new LoginResponseDTO(
                token,
                usuarioDetails.getUsername(),
                usuarioDetails.getUsuario().getRol().name()
        ));
    }
}