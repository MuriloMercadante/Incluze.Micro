package br.com.fiap.aether.exception;

/** Lançada quando um recurso solicitado não existe (HTTP 404). */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String recurso, Long id) {
        super(recurso + " com id " + id + " não encontrado(a)");
    }
}
