package com.ediluana.minhasfinancas.model.repository;

import com.ediluana.minhasfinancas.model.entity.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {


}
