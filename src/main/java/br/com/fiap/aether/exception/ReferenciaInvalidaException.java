package br.com.fiap.aether.exception;

/**
 * Lançada quando o corpo da requisição referencia um recurso que não existe
 * (ex.: usuarioId inexistente ao criar uma avaliação) - HTTP 422.
 *
 * Diferente do 404, que indica que o recurso da URL não existe: aqui a URL é
 * válida, mas o conteúdo enviado não pode ser processado.
 */
public class ReferenciaInvalidaException extends RuntimeException {

    public ReferenciaInvalidaException(String campo, String recurso, Long id) {
        super("O campo " + campo + " referencia " + recurso + " inexistente (id " + id + ")");
    }
}
