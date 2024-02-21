package com.ediluana.minhasfinancas.model.repository;

import com.ediluana.minhasfinancas.model.entity.Lancamento;
import com.ediluana.minhasfinancas.model.enums.StatusLancamento;
import com.ediluana.minhasfinancas.model.enums.TipoLancamento;
import static org.assertj.core.api.Assertions.*; // sem statico, precisa do Assertions.assert ..
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest // deleta a base de dados de teste
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // não sobrescreve configuracoes,
public class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void deveSalvarUmLancamento() {
        //cenario
        Lancamento lancamento = criarLancamento();

        lancamento = repository.save(lancamento);

        assertThat(lancamento.getId()).isNotNull();

    }

    @Test
    public void deveDeletarUmLancamento() {
        Lancamento lancamento = criarEPersistirLancamento();

        lancamento = entityManager.find(Lancamento.class, lancamento.getId()); // buscar lancamento persistido

        repository.delete(lancamento);

        Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
        assertThat(lancamentoInexistente).isNull();

    }

    @Test
    public void deveAtualizarUmLancamento() {
        Lancamento lancamento = criarEPersistirLancamento();

        lancamento.setAno(2020);
        lancamento.setDescricao("Teste Atualizar");
        lancamento.setStatus(StatusLancamento.CANCELADO);
        repository.save(lancamento);

        Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
        assertThat(lancamentoAtualizado).isEqualTo(lancamento);
    }
    
    @Test
    public void deveBuscarUmLancamentoPorId() {
        Lancamento lancamento = criarEPersistirLancamento();

        Optional<Lancamento> lancamentoEncontrato = repository.findById(lancamento.getId());

        assertThat(lancamentoEncontrato.isPresent()).isTrue();

    }


    public static Lancamento criarLancamento() {
        return Lancamento.builder().ano(2019).mes(1).descricao("lançamento qualquer").valor(BigDecimal.valueOf(10)).tipo(TipoLancamento.RECEITA).status(StatusLancamento.PENDENTE).dataCadastro(Calendar.getInstance()).build();
    }

    private Lancamento criarEPersistirLancamento() {
        Lancamento lancamento = criarLancamento();
        entityManager.persist(lancamento); //persistir lancamento
        return lancamento;
    }


}
