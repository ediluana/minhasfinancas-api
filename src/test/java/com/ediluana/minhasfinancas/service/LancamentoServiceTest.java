package com.ediluana.minhasfinancas.service;

import com.ediluana.minhasfinancas.excepition.RegraNegocioException;
import com.ediluana.minhasfinancas.model.entity.Lancamento;
import com.ediluana.minhasfinancas.model.entity.Usuario;
import com.ediluana.minhasfinancas.model.enums.StatusLancamento;
import com.ediluana.minhasfinancas.model.repository.LancamentoRepository;
import com.ediluana.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.ediluana.minhasfinancas.service.impl.LancamentoServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest //não precisa dessa anotaçãp caso seja teste unitário
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

    @SpyBean // chama os métodos reais
    LancamentoServiceImpl service;

    @MockBean //simulação comportamento
    LancamentoRepository repository;

    @Test
    public void deveSalvarUmLancamento() {
        //cenário
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doNothing().when(service).validar(lancamentoASalvar);

        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1l);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

        //execucao
        Lancamento lancamento = service.salvar(lancamentoASalvar);

        //verificacao
        Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
        Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
    }

    @Test
    public void naoDeveSalvarUmLancamentoQuandoHouverErroValidadao() {
        //cenario
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

        //execucao e verificacao
        Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class); //verifica tbm o tipo do erro
        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
    }


    @Test
    public void deveAtualizarUmLancamento() {
        //cenário
        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1l);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        Mockito.doNothing().when(service).validar(lancamentoSalvo);
        Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        //execucao
        service.atualizar(lancamentoSalvo);

        //verificacao
        Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
    }

    @Test
    public void deveLancarErroAoTentarAtualizarLancamentoNaoSalvo() {
        //cenario
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        //execucao e verificacao
        Assertions.catchThrowableOfType(() -> service.atualizar(lancamento), NullPointerException.class); //verifica tbm o tipo do erro
        Mockito.verify(repository, Mockito.never()).save(lancamento);
    }

    @Test
    public void deveDeletarLancamento() {
        //cenario
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);

        //excecucao
        service.deletar(lancamento);

        //verificacao
        Mockito.verify(repository).delete(lancamento);

    }

    @Test
    public void deveLancarErroAoTentarDeletarLancamentoNaoSalvo() {
        //cenario
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        //excecucao
        Assertions.catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);

        //verificacao
        Mockito.verify(repository, Mockito.never()).delete(lancamento);
    }

    @Test
    public void deveFiltrarLancamentos() {
        //cenario
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);

        List<Lancamento> lista = Arrays.asList(lancamento); // pega vários objetos e retorna como lista
        Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

        //execucao
        List<Lancamento> resultado = service.buscar(lancamento);

        //verificacao
        Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);

    }

    @Test
    public void deveAtualizarStatusLancamento() {
        //cenario
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);
        lancamento.setStatus(StatusLancamento.PENDENTE);

        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
        Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

        //execucao
        service.atualizarStatus(lancamento, novoStatus);

        //verificacao
        Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
        Mockito.verify(service).atualizar(lancamento);

    }

    @Test
    public void deveObterLancamentoPorId() {
        //cenario
        Long id = 1l;

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

        //execucao
        Optional<Lancamento> resultado = service.obterPorId(id);

        //verificacao
        Assertions.assertThat(resultado.isPresent()).isTrue();
    }

    @Test
    public void deveRetornarVazioQuandoLancamentoNaoExiste() {
        //cenario
        Long id = 1l;

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        //execucao
        Optional<Lancamento> resultado = service.obterPorId(id);

        //verificacao
        Assertions.assertThat(resultado.isPresent()).isFalse();
    }


    @Test
    public void deveRetornarExceptionsAoValidarLancamento() {
        //cenario
        Lancamento lancamento = new Lancamento();

        Throwable erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");
        lancamento.setDescricao("");

        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");
        lancamento.setDescricao("teste");

        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
        lancamento.setMes(0);

        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
        lancamento.setMes(13);

        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
        lancamento.setMes(8);

        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
        lancamento.setAno(202);

        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
        lancamento.setAno(2023);

        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");
        lancamento.setUsuario(new Usuario());

        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");
        lancamento.getUsuario().setId(1L);

        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Valor válido.");
        lancamento.setValor(BigDecimal.ZERO);

        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Valor válido.");
        lancamento.setValor(BigDecimal.valueOf(25899));

        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Tipo de lançamento.");


    }


}
