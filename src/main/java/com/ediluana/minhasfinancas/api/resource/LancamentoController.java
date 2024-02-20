package com.ediluana.minhasfinancas.api.resource;

import com.ediluana.minhasfinancas.api.dto.AtualizaStatusDTO;
import com.ediluana.minhasfinancas.api.dto.LancamentoDTO;
import com.ediluana.minhasfinancas.excepition.RegraNegocioException;
import com.ediluana.minhasfinancas.model.entity.Lancamento;
import com.ediluana.minhasfinancas.model.entity.Usuario;
import com.ediluana.minhasfinancas.model.enums.StatusLancamento;
import com.ediluana.minhasfinancas.model.enums.TipoLancamento;
import com.ediluana.minhasfinancas.service.LancamentoService;
import com.ediluana.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lancamentos")
public class LancamentoController {

    private final LancamentoService service;
    private final UsuarioService usuarioService;

    //        public LancamentoController(LancamentoService service, UsuarioService usuarioService) { // podendo adicionar final nas declarações e tbm adicionando
    //            // a anotation @RequiredArgsConstructor
    //            this.service = service;
    //            this.usuarioService = usuarioService;
    //        }

    @GetMapping
    public ResponseEntity buscar( // podendo ser uma mapa de parametros
                                  @RequestParam(value = "descricao", required = false) String descricao, @RequestParam(value = "mes", required = false) Integer mes, @RequestParam(value = "ano", required = false) Integer ano, @RequestParam("usuario") Long idUsuario) {

        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);

        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
        if (!usuario.isPresent()) {
            return ResponseEntity.badRequest().body("Não possível realizar a consulta. Usuário não encontrado para o Id informado");

        } else {
            lancamentoFiltro.setUsuario(usuario.get());
        }
        List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
        return ResponseEntity.ok(lancamentos);

    }

    @PostMapping // criar recurso / servidor que ainda não foi criado
    public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
        try {
            Lancamento entidade = converter(dto);
            entidade = service.salvar(entidade);
            return new ResponseEntity(entidade, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}") // atualizar recurso que esta no servidor passando valor // return created ou ok
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
        return service.obterPorId(id).map(entity -> { // lancamento encontrado
            try {
                Lancamento lancamento = converter(dto);
                lancamento.setId(entity.getId());
                service.atualizar(lancamento);
                return ResponseEntity.ok(lancamento);
            } catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST)); //se não
    }

    @PutMapping("{id}/atualiza-status")
    public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
        return service.obterPorId(id).map(entity -> {
            StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());

            if (statusSelecionado == null) {
                return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento, envie um status válido.");
            }

            try {
                entity.setStatus(statusSelecionado);
                service.atualizar(entity);
                return ResponseEntity.ok(entity);

            } catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST)); //se não
    }

    @DeleteMapping("{id}")
    public ResponseEntity deletar(@PathVariable("id") Long id) {
        return service.obterPorId(id).map(entidade -> {
            service.deletar(entidade);
            return new ResponseEntity(HttpStatus.NO_CONTENT); //não precisa retornar
        }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST)); //se não
    }

    private Lancamento converter(LancamentoDTO dto) {
        Lancamento lancamento = new Lancamento();
        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());

        Usuario usuario = usuarioService.obterPorId(dto.getUsuario()).orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o Id informado"));

        lancamento.setUsuario(usuario);

        if (dto.getTipo() != null) lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));

        if (dto.getStatus() != null) lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));

        return lancamento;
    }

}
