package com.vinicius.usuario.business;

import com.vinicius.usuario.business.converter.UsuarioConverter;
import com.vinicius.usuario.business.dto.EnderecoDTO;
import com.vinicius.usuario.business.dto.TelefoneDTO;
import com.vinicius.usuario.business.dto.UsuarioDTO;
import com.vinicius.usuario.infraestructure.entity.Endereco;
import com.vinicius.usuario.infraestructure.entity.Telefone;
import com.vinicius.usuario.infraestructure.entity.Usuario;
import com.vinicius.usuario.infraestructure.exceptions.ConflictException;
import com.vinicius.usuario.infraestructure.exceptions.ResourceNotFoundException;
import com.vinicius.usuario.infraestructure.repository.EnderecoRepository;
import com.vinicius.usuario.infraestructure.repository.TelefoneRepository;
import com.vinicius.usuario.infraestructure.repository.UsuarioRepository;
import com.vinicius.usuario.infraestructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;


    public UsuarioDTO salvarUsuario(UsuarioDTO usuarioDTO) {
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        return usuarioConverter.paraUsuarioDTO(
                usuarioRepository.save(usuario)
        );
    }

    public void emailExiste(String email) {
        try {
            boolean existe = verificaEmailExistente(email);
            if (existe) {
                throw new ConflictException("Email já cadastrado " + email);
            }
        } catch (ConflictException e) {
            throw new ConflictException("Email já cadastrado ", e.getCause());
        }
    }

    public boolean verificaEmailExistente(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public UsuarioDTO buscarUsuarioPorEmail (String email) {
        try {
            return usuarioConverter.paraUsuarioDTO(
                    usuarioRepository.findByEmail(email)
                            .orElseThrow(
                    () -> new ResourceNotFoundException("Email não encontrado " + email)
                            )
            );
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Email não encontrado" + email);
        }
    }

    public void deletaUsuarioPorEmail (String email) {
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO atualizaDadosUsuario (String token, UsuarioDTO dto) {
        // Aqui buscamos o email do usuário através do token (tirar a obrigatoriedade do email)
        String email = jwtUtil.extrairEmailToken(token.substring(7));

        // Criptografia de senha
        dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);

        // Busca os dados do usuário do banco de dados
        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email nã localizado"));

        //Mesclou os dados que recebemos na requisição DTO com os dados do banco de dados
        Usuario usuario = usuarioConverter.updateUsuario(dto, usuarioEntity);

        // salvou os dados do usuario convertido e depois pegou o retorno e converteu para UsuarioDTO
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));

    }

    public EnderecoDTO atualizaEndereco(Long idEndereco, EnderecoDTO enderecoDTO) {
        // Recebe as informações já salvas no banco de dados
        Endereco entity = enderecoRepository.findById(idEndereco).orElseThrow(()->
                new ResourceNotFoundException("Id não encontrado."));

        // Converte os dados atualizando o que foi mudado e mantendo o que não sofreu alteração
        Endereco endereco = usuarioConverter.updateEndereco(enderecoDTO,entity);

        // salva os dados convertidos e depois retorna os dados convertidos para a DTO.
        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));

    }

    public TelefoneDTO atualizaTelefone(Long idTelefone, TelefoneDTO telefoneDTO) {
        // Recebe as informações já salvas no banco de dados
        Telefone entity = telefoneRepository.findById(idTelefone).orElseThrow(()->
                new ResourceNotFoundException("Id não encontrado."));

        // Converte os dados atualizando o que foi mudado e mantendo o que não sofreu alteração
        Telefone telefone = usuarioConverter.updateTelefone(telefoneDTO, entity);

        // salva os dados convertidos e depois retorna os dados convertidos para a DTO.
        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));

    }

    public EnderecoDTO cadastraEndereco(String token, EnderecoDTO dto) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email não localizado"));

        Endereco endereco = usuarioConverter.paraEnderecoEntity(dto, usuario.getId());

        return usuarioConverter.paraEnderecoDTO(
                enderecoRepository.save(endereco)
        );

    }

    public TelefoneDTO cadastraTelefone(String token, TelefoneDTO dto) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email não localizado"));

        Telefone telefone = usuarioConverter.paraTelefoneEntity(dto, usuario.getId());

        return usuarioConverter.paraTelefoneDTO(
                telefoneRepository.save(telefone)
        );
    }

}
