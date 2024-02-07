package com.ediluana.minhasfinancas.service.impl;

import com.ediluana.minhasfinancas.excepition.ErroAutenticacao;
import com.ediluana.minhasfinancas.excepition.RegraNegocioException;
import com.ediluana.minhasfinancas.model.entity.Usuario;
import com.ediluana.minhasfinancas.model.repository.UsuarioRepository;
import com.ediluana.minhasfinancas.service.UsuarioService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    public UsuarioServiceImpl(UsuarioRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuario = repository.findByEmail(email);

        if (!usuario.isPresent()) {
            throw new ErroAutenticacao("Usuário não encontrado para o email informado");
        }

        if (!usuario.get().getSenha().equals(senha)) {
            throw new ErroAutenticacao("Senha inválida");
        }

        return usuario.get();
    }

    @Override
    @Transactional // criar base de dados, abrir uma transação e executar para depois commitar
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return repository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = repository.existsByEmail(email);
        if (existe) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este email.");
        }
    }
}
