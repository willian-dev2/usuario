package com.vinicius.usuario.business.converter;

import com.vinicius.usuario.business.dto.EnderecoDTO;
import com.vinicius.usuario.business.dto.TelefoneDTO;
import com.vinicius.usuario.business.dto.UsuarioDTO;
import com.vinicius.usuario.infraestructure.entity.Endereco;
import com.vinicius.usuario.infraestructure.entity.Telefone;
import com.vinicius.usuario.infraestructure.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UsuarioConverter {

    // Conversão dos nossos DTOS para nossas ENTITYS

    public Usuario paraUsuario(UsuarioDTO usuarioDTO) {
        return Usuario.builder()
                .nome(usuarioDTO.getNome())
                .email(usuarioDTO.getEmail())
                .senha(usuarioDTO.getSenha())
                .enderecos(paraListaEndereco(usuarioDTO.getEndereco()))
                .telefones(paraListaTelefone(usuarioDTO.getTelefone()))
                .build();
    }

    // Método mais complexo de converter mossa collection... OBS: dá para fazer tambem com for, como no exemplo de telefone
    public List<Endereco> paraListaEndereco(List<EnderecoDTO> enderecoDTOS) {
        return enderecoDTOS.stream().map(this::paraEndereco).toList();
    }

    public Endereco paraEndereco(EnderecoDTO enderecoDTO) {
        return Endereco.builder()
                .rua(enderecoDTO.getRua())
                .numero(enderecoDTO.getNumero())
                .cidade(enderecoDTO.getCidade())
                .estado(enderecoDTO.getEstado())
                .cep(enderecoDTO.getCep())
                .complemento(enderecoDTO.getComplemento())
                .build();
    }

    // método mais simples de converter uma collection
    public List<Telefone> paraListaTelefone(List<TelefoneDTO> telefoneDTOS) {
        List<Telefone> telefones = new ArrayList<>();
        for(TelefoneDTO telefoneDTO : telefoneDTOS) {
            telefones.add(paraTelefone(telefoneDTO));
        }
        return telefones;
    }

    public Telefone paraTelefone(TelefoneDTO telefoneDTO) {
        return Telefone.builder()
                .ddd(telefoneDTO.getDdd())
                .numero(telefoneDTO.getNumero())
                .build();
    }


    // Conversão da nossa ENTITY para nossos DTOS

    public UsuarioDTO paraUsuarioDTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .senha(usuario.getSenha())
                .endereco(paraListaEnderecoDTO(usuario.getEnderecos()))
                .telefone(paraListaTelefoneDTO(usuario.getTelefones()))
                .build();
    }

    // Método mais complexo de converter mossa collection... OBS: dá para fazer tambem com for, como no exemplo de telefone
    public List<EnderecoDTO> paraListaEnderecoDTO(List<Endereco> endereco) {
        return endereco.stream().map(this::paraEnderecoDTO).toList();
    }

    public EnderecoDTO paraEnderecoDTO(Endereco endereco) {
        return EnderecoDTO.builder()
                .id(endereco.getId())
                .rua(endereco.getRua())
                .numero(endereco.getNumero())
                .cidade(endereco.getCidade())
                .estado(endereco.getEstado())
                .cep(endereco.getCep())
                .complemento(endereco.getComplemento())
                .build();
    }

    // método mais simples de converter uma collection
    public List<TelefoneDTO> paraListaTelefoneDTO(List<Telefone> telefoneDTOS) {
        List<TelefoneDTO> telefones = new ArrayList<>();
        for(Telefone telefone : telefoneDTOS) {
            telefones.add(paraTelefoneDTO(telefone));
        }
        return telefones;
    }

    public TelefoneDTO paraTelefoneDTO(Telefone telefone) {
        return TelefoneDTO.builder()
                .id(telefone.getId())
                .ddd(telefone.getDdd())
                .numero(telefone.getNumero())
                .build();
    }

    // Aqui fazemos uma conversão de dados para tirar a obrigatoriedade da requisição da nossa controller
    // de passar todos os dados ao invés de apenas o que deseja atualizar.
    public Usuario updateUsuario(UsuarioDTO usuarioDTO, Usuario entity) {
        return Usuario.builder()

                // se o nome de usuarioDTO não é nulo, pegue ele, se não, pega da nossa entity.
                // Isso evita algum campo retornar nulo ou erro para a service e, logo, controller
                .nome(usuarioDTO.getNome() != null ? usuarioDTO.getNome() : entity.getNome())
                .id(entity.getId())
                .senha(usuarioDTO.getSenha() != null ? usuarioDTO.getSenha() : entity.getSenha())
                .email(usuarioDTO.getEmail() != null ? usuarioDTO.getEmail() : entity.getEmail())
                .enderecos(entity.getEnderecos())
                .telefones(entity.getTelefones())
                .build();
    }

    public Endereco updateEndereco(EnderecoDTO dto, Endereco entity) {
        return Endereco.builder()
                .id(entity.getId())
                .cidade(dto.getCidade() != null ? dto.getCidade() : entity.getCidade())
                .rua(dto.getRua() != null ? dto.getRua() : entity.getRua())
                .complemento(dto.getComplemento() != null ? dto.getComplemento() : entity.getComplemento())
                .cep(dto.getCep() != null ? dto.getCep() : entity.getCep())
                .estado(dto.getEstado() != null ? dto.getEstado() : entity.getEstado())
                .numero(dto.getNumero() != null ? dto.getNumero() : entity.getNumero())
                .build();
    }

    public Telefone updateTelefone(TelefoneDTO dto, Telefone entity) {
        return Telefone.builder()
                .id(entity.getId())
                .numero(dto.getNumero() != null ? dto.getNumero() : entity.getNumero())
                .ddd(dto.getDdd() != null ? dto.getDdd() : entity.getDdd())
                .build();
    }

    public Endereco paraEnderecoEntity(EnderecoDTO dto, Long idUsuario) {
        return Endereco.builder()
                .rua(dto.getRua())
                .cidade(dto.getCidade())
                .cep(dto.getCep())
                .numero(dto.getNumero())
                .estado(dto.getEstado())
                .complemento(dto.getComplemento())
                .usuario_id(idUsuario)
                .build();
    }

    public Telefone paraTelefoneEntity(TelefoneDTO dto, Long idTelefone) {
        return Telefone.builder()
                .ddd(dto.getDdd())
                .numero(dto.getNumero())
                .usuario_id(idTelefone)
                .build();
    }

}
