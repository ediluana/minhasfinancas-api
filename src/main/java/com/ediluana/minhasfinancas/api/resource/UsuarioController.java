package com.ediluana.minhasfinancas.api.resource;

import com.ediluana.minhasfinancas.api.dto.UsuarioDTO;
import com.ediluana.minhasfinancas.excepition.ErroAutenticacao;
import com.ediluana.minhasfinancas.excepition.RegraNegocioException;
import com.ediluana.minhasfinancas.model.entity.Usuario;
import com.ediluana.minhasfinancas.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Juncao de @Controller e @ResponseBody (diz que todos os metodos com return são corpo da resposta
@RequestMapping("/api/usuarios") //raiz
public class UsuarioController {
    //Resource

    private UsuarioService service;

//    @GetMapping("/") //mapeando método para requisicao com metodo get para url ""
//    public String helloWorld() {
//        return "hello world!";
//    }

    public UsuarioController(UsuarioService service) { //poderia ter usado anotation @Autowired
        this.service = service;
    }

    @PostMapping("/autenticar") //dá erro se estiver o msm
    public ResponseEntity autenticar(@RequestBody UsuarioDTO dto) {
        try {
            Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
            return ResponseEntity.ok(usuarioAutenticado); // 200 operação realizada com sucesso
        } catch (ErroAutenticacao e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody UsuarioDTO dto) { //Recebe DTO

        Usuario usuario = Usuario.builder().nome(dto.getNome()).email(dto.getEmail()).senha(dto.getSenha()).build();

        try {
            Usuario usuarioSalvo = service.salvarUsuario(usuario);
            return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED); // recebe objeto
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }


}