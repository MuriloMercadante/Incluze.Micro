package br.com.fiap.aether.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Corpo padronizado de todas as respostas de erro da API.
 * Nunca contém stack trace, mensagens de exceção internas ou dados sensíveis.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<CampoInvalido> campos) {

    public record CampoInvalido(String campo, String mensagem) {
    }
}
