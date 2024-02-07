package com.ediluana.minhasfinancas.excepition;

public class ErroAutenticacao extends RuntimeException {

    public ErroAutenticacao(String mensagem) {
        super(mensagem);
    }

}
