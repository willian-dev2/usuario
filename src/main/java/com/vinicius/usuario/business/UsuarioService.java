package com.vinicius.usuario.business;

import com.vinicius.usuario.business.converter.UsuarioConverter;
import com.vinicius.usuario.business.dto.UsuarioDTO;
import com.vinicius.usuario.infraestructure.entity.Usuario;
import com.vinicius.usuario.infraestructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;


    public UsuarioDTO salvarUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        return usuarioConverter.paraUsuarioDTO(
                usuarioRepository.save(usuario)
        );
    }


}
