package com.ediluana.minhasfinancas.model.repository;

import com.ediluana.minhasfinancas.model.entity.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;


//@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest // deleta a base de dados de teste
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // não sobrescreve configuracoes,
public class UsuarioRepositoryTest {

    //mais simples possível, somente para testas a funcionalidade.

    @Autowired
    UsuarioRepository repository;

    @Autowired
    TestEntityManager entityManager; // classe responsável por fazer as operações na base de dados

    public static Usuario criarUsuario(){
        return Usuario.builder().nome("usuario").email("ediluana@unicamp.br").senha("senha").build();
    }

    @Test
    public void verificarAExistenciaDeUmEmail() {

        //cenário
        Usuario usuario = criarUsuario();
        //repository.save(usuario);
        entityManager.persist(usuario);

        //ação / execução
        boolean result = repository.existsByEmail("ediluana@unicamp.br");

        //verificação
        Assertions.assertThat(result).isTrue();
    }


    @Test
    public void falsoQuandoNaoHouverUsuarioCadastradoComEmail() {

        //cenário
        //repository.deleteAll();

        //ação
        boolean result = repository.existsByEmail("ediluana@unicamp.br");

        //verificacao
        Assertions.assertThat(result).isFalse();

    }

    @Test
    public void persistirUmUsuarioNaBaseDeDados() {
        //cenario
        Usuario usuario = criarUsuario();

        //acao
        Usuario usuarioSalvo = repository.save(usuario);

        //verificacao
        Assertions.assertThat(usuarioSalvo.getId()).isNotNull();

    }

    @Test
    public void buscarUmUsuarioPorEmail(){

        //cenario
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario); //não pode ter id, se não lança exceçao

        //verificacao
        Optional<Usuario> result = repository.findByEmail(usuario.getEmail());

        Assertions.assertThat(result.isPresent()).isTrue();


    }

    @Test
    public void retornarVazioAoBuscarUmUsuarioPorEmailQuandoNaoExisteNaBase(){

        //verificacao
        Optional<Usuario> result = repository.findByEmail("ediluana@unicamp.br");

        Assertions.assertThat(result.isPresent()).isFalse();


    }



}
