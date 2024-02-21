package com.ediluana.minhasfinancas.api.resource;

import com.ediluana.minhasfinancas.api.dto.UsuarioDTO;
import com.ediluana.minhasfinancas.excepition.ErroAutenticacao;
import com.ediluana.minhasfinancas.excepition.RegraNegocioException;
import com.ediluana.minhasfinancas.model.entity.Usuario;
import com.ediluana.minhasfinancas.service.LancamentoService;
import com.ediluana.minhasfinancas.service.UsuarioService;
import com.ediluana.minhasfinancas.service.impl.UsuarioServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioController.class) // subir contexto rest apenas para testar o controller (isolar teste)
@AutoConfigureMockMvc // ter acesso ao objeto para ajudar nas exceucoes
public class UsuarioControllerTest {

    static final String API = "/api/usuarios";
    static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Autowired
    MockMvc mvc; // serve para mockar todas as chamadas
    @MockBean
    UsuarioService service;
    @MockBean
    LancamentoService lancamentoService;

    @Test //teste unitário
    public void deveAutenticarUmUsuario() throws Exception { //front utilizando json
        //cenario
        String email = "ediluana@unicamp.br";
        String senha = "123";
        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build(); //json
        Usuario usuario = Usuario.builder().id(1L).email(email).senha(senha).build();

        Mockito.when(service.autenticar(email, senha)).thenReturn(usuario);

        String json = new ObjectMapper().writeValueAsString(dto); //pega um objeto e transforma em string json

        //execucao e verificacao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar")).accept(JSON).contentType(JSON).content(json);
        //criar requisicao, aceitando aplicacao json, mandando e recebendo

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk()) //executa a requisicao
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId())).andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome())).andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
    }

    @Test
    public void deveRetornarBadRequestAoObterErroDeAutenticacao() throws Exception { //front utilizando json
        //cenario
        String email = "ediluana@unicamp.br";
        String senha = "123";
        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build(); //json
        Usuario usuario = Usuario.builder().id(1L).email(email).senha(senha).build();

        Mockito.when(service.autenticar(email, senha)).thenThrow(ErroAutenticacao.class);

        String json = new ObjectMapper().writeValueAsString(dto); //pega um objeto e transforma em string json

        //execucao e verificacao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar")).accept(JSON).contentType(JSON).content(json);
        //criar requisicao, aceitando aplicacao json, mandando e recebendo

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest()); //executa a requisicao
        //                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
        //                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
        //                .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
    }

    @Test //teste unitário
    public void deveCriarUmNovoUsuario() throws Exception { //front utilizando json
        //cenario
        String email = "ediluana@unicamp.br";
        String senha = "123";
        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build(); //json
        Usuario usuario = Usuario.builder().id(1L).email(email).senha(senha).build();

        Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);

        String json = new ObjectMapper().writeValueAsString(dto); //pega um objeto e transforma em string json

        //execucao e verificacao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API).accept(JSON).contentType(JSON).content(json);
        //criar requisicao, aceitando aplicacao json, mandando e recebendo

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated()) //executa a requisicao
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId())).andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome())).andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
    }

    @Test //teste unitário
    public void deveRetornarBadRequestAoTentarCriarUmUsuarioInvalido() throws Exception { //front utilizando json
        //cenario
        String email = "ediluana@unicamp.br";
        String senha = "123";
        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build(); //json

        Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);

        String json = new ObjectMapper().writeValueAsString(dto); //pega um objeto e transforma em string json

        //execucao e verificacao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API).accept(JSON).contentType(JSON).content(json);
        //criar requisicao, aceitando aplicacao json, mandando e recebendo

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest()); //executa a requisicao

    }

}
