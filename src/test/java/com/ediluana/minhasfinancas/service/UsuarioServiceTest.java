package com.ediluana.minhasfinancas.service;

import com.ediluana.minhasfinancas.excepition.ErroAutenticacao;
import com.ediluana.minhasfinancas.excepition.RegraNegocioException;
import com.ediluana.minhasfinancas.model.entity.Usuario;
import com.ediluana.minhasfinancas.model.repository.UsuarioRepository;
import com.ediluana.minhasfinancas.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat; //JUnit5 não tem funcao assertThat, precisa do hamcrest

@SpringBootTest //não precisa dessa anotaçãp caso seja teste unitário
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    //@Autowired // teste unitário não precisa injetar
    //UsuarioService service;

    //@Autowired // teste unitário não precisa injetar
    @MockBean
    UsuarioRepository repository; //Mock gerenciado

    @SpyBean
    UsuarioServiceImpl service;

    //FORMA PADRAO DE CRIAR MOCK E SPY
//    @BeforeEach //sinalizar que o método anotado deve ser executado antes de cada invocação do método @Test
//    public void setUp() {
//        //repository = Mockito.mock(UsuarioRepository.class); //instancia de mock / classe fake sem a anotation @MockBean
//
//        service = Mockito.spy(UsuarioServiceImpl.class); // criar spy
//
//        //service = new UsuarioServiceImpl(repository); // passando repository mockado
//    }

    @Test
    public void salvarUmUsuario() {
        //cenario
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString()); //fazer nada, não lançar erro
        Usuario usuario = Usuario.builder().nome("nome").email("ediluana@unicamp.br").senha("senha").id(1L).build();

        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        //acao
        Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
        Assertions.assertDoesNotThrow(() -> service.salvarUsuario(new Usuario()));

        //verificacao
        Assertions.assertDoesNotThrow(() -> usuarioSalvo);
        assertThat(usuarioSalvo, is(notNullValue()));
        assertThat(usuarioSalvo.getNome(), is(equalTo("nome")));
        assertThat(usuarioSalvo.getEmail(), is(equalTo("ediluana@unicamp.br")));
        assertThat(usuarioSalvo.getSenha(), is(equalTo("senha")));

    }

    @Test
    public void naoSalvarUsuarioComEmailJaCadastrado() {
        //cenario
        String email = "ediluana@unicamp.br";
        Usuario usuario = Usuario.builder().email(email).build();
        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email); //exiba o exception quando chamar método

        //acao
        Assertions.assertThrows(RegraNegocioException.class, () -> service.salvarUsuario(usuario));

        //verificacao
        Mockito.verify(repository, Mockito.never()).save(usuario); // Verificando que nunca chame o método
    }


    @Test
    public void autenticarUmUsuarioComSucesso() {
        //cenario
        String email = "ediluana@unicamp.br";
        String senha = "senha";

        Usuario usuario = Usuario.builder().email(email).senha(senha).id(1L).build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

        //acao
        Usuario result = service.autenticar(email, senha);

        //verificacao
        assertThat(true, is(notNullValue())); // Opcional, mas consegue fazer Combinação de múltiplas condições, verifica conteudo de listas, etc.
        Assertions.assertDoesNotThrow(() -> service.autenticar(email, senha)); // garante que não lançou nenhuma excecao () -> expressões lambda
    }

    @Test
    public void lancarErroQuandoUsuarioNaoCadastradoComEmailInformado() {
        //cenario
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        //acao e verificacao
        //service.autenticar("ediluana@unicamp.br", "senha");
        //Assertions.assertThrows(ErroAutenticacao.class, () -> service.autenticar("ediluana@unicamp.br", "senha"));

        ErroAutenticacao exception = Assertions.assertThrows(ErroAutenticacao.class, () -> service.autenticar("ediluana@unicamp.br", "senha"));
        assertThat(exception.getMessage(), is("Usuário não encontrado para o email informado"));
    }

    @Test
    public void lancarErroQuandoSenhaNaoBater() {
        //cenario
        String senha = "senha";
        Usuario usuario = Usuario.builder().email("ediluana@unicamp.br").senha(senha).build();
        Mockito.when(repository.findByEmail((Mockito.anyString()))).thenReturn(Optional.of(usuario));

        //acao
        //service.autenticar("ediluana@unicamp.br", "123");

        //Assertions.assertThrows(ErroAutenticacao.class, () -> service.autenticar("ediluana@unicamp.br", "123"));

        ErroAutenticacao exception = Assertions.assertThrows(ErroAutenticacao.class, () -> service.autenticar("ediluana@unicamp.br", "123"));
        //Assertions.assertEquals("Senha inválida", exception.getMessage()); // também funcionaria
        assertThat(exception.getMessage(), is("Senha inválida"));
    }


    @Test
    public void deveValidarEmail() {
        //cenário
        //repository.deleteAll();
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        //acao
        //service.validarEmail("ediluana@unicamp.br");

        //acao e verificacao
        //@Test(Expected = Test.None.class)
        Assertions.assertDoesNotThrow(() -> service.validarEmail("ediluana@unicamp.br")); //valida para não retornar nenhum erro
    }

    @Test //(Expec = RegraNegocioException.class)
    public void deveLancarErroSeExistirEmailCadastrado() {

        //cenario
//        Usuario usuario = Usuario.builder().nome("Usuario").email("ediluana@unicamp.br").build();
//        repository.save(usuario);
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        //acao
        //  service.validarEmail("ediluana@unicamp.br");

        //acao e verificacao
        Assertions.assertThrows(RegraNegocioException.class, () -> service.validarEmail("ediluana@unicamp.br"));

    }

}
