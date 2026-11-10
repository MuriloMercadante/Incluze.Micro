package br.com.fiap.aether.exception;

/** Lançada quando uma regra de negócio impede a operação (HTTP 409). */
public class BusinessException extends RuntimeException {

    public BusinessException(String mensagem) {
        super(mensagem);
    }
}
