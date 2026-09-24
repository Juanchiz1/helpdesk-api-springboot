package com.juandiego.sistema_tickets_soporte.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.juandiego.sistema_tickets_soporte.exception.EmailYaExisteException;
import com.juandiego.sistema_tickets_soporte.exception.RecursoNoEncontradoException;
import com.juandiego.sistema_tickets_soporte.model.Usuario;
import com.juandiego.sistema_tickets_soporte.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new EmailYaExisteException(
                    "Ya existe un usuario con el email: " + usuario.getEmail());
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con id: " + id));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con email: " + email));
    }

    public List<Usuario> listarTodos() {
    return usuarioRepository.findAll();
}

public List<Usuario> listarPorRol(Usuario.Rol rol) {
    return usuarioRepository.findByRol(rol);
}

public Usuario cambiarEstadoActivo(Long id, boolean activo) {
    Usuario usuario = buscarPorId(id);
    usuario.setActivo(activo);
    return usuarioRepository.save(usuario);
}
}