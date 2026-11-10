package br.com.fiap.aether.exception;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.fiap.aether.dto.ErrorResponse;
import br.com.fiap.aether.dto.ErrorResponse.CampoInvalido;

/**
 * Tratamento centralizado de erros. Todas as respostas de erro seguem o
 * formato {@link ErrorResponse} e nunca expõem stack traces ou detalhes internos.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 404 - recurso inexistente. */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, List.of());
    }

    /** 422 - o corpo referencia um recurso inexistente (ex.: usuarioId que não existe). */
    @ExceptionHandler(ReferenciaInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleReferenciaInvalida(ReferenciaInvalidaException ex, WebRequest request) {
        return build(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage(), request, List.of());
    }

    /**
     * 503 - banco de dados indisponível (ex.: Azure SQL serverless retomando após pausa).
     * O cliente recebe Retry-After em vez de um 500 genérico.
     */
    @ExceptionHandler({ CannotCreateTransactionException.class, DataAccessResourceFailureException.class })
    public ResponseEntity<ErrorResponse> handleBancoIndisponivel(Exception ex, WebRequest request) {
        log.warn("Banco de dados indisponível: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .header(HttpHeaders.RETRY_AFTER, "30")
                .body(body(HttpStatus.SERVICE_UNAVAILABLE,
                        "Banco de dados temporariamente indisponível. Tente novamente em alguns segundos.",
                        request, List.of()));
    }

    /** 409 - regra de negócio violada (ex.: e-mail duplicado). */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, WebRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request, List.of());
    }

    /** 409 - violação de integridade no banco (detalhes do SQL ficam apenas no log). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, WebRequest request) {
        log.warn("Violação de integridade: {}", ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT,
                "A operação viola uma restrição de integridade dos dados", request, List.of());
    }

    /** 500 - falha inesperada: mensagem genérica, detalhes apenas no log. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, WebRequest request) {
        log.error("Erro inesperado", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno. Tente novamente mais tarde.", request, List.of());
    }

    /** 400 - Bean Validation (@Valid) com a lista de campos inválidos. */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        // Falha de conversão (ex.: "abc" em campo numérico, enum inexistente) traria
        // nomes de classes Java na mensagem padrão: substituída por texto neutro.
        List<CampoInvalido> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new CampoInvalido(fe.getField(), fe.isBindingFailure()
                        ? "Valor inválido para o campo '" + fe.getField() + "'"
                        : fe.getDefaultMessage()))
                .toList();
        return new ResponseEntity<>(
                body(HttpStatus.BAD_REQUEST, "Dados de entrada inválidos", request, campos),
                headers, HttpStatus.BAD_REQUEST);
    }

    /**
     * Demais erros do Spring MVC (JSON malformado, tipo de parâmetro inválido,
     * rota inexistente, método não suportado, etc.) no mesmo formato padronizado.
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
            HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        HttpStatus status = HttpStatus.resolve(statusCode.value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        String mensagem = switch (status) {
            case BAD_REQUEST -> "Requisição inválida. Verifique o corpo JSON, os parâmetros e os tipos enviados";
            case NOT_FOUND -> "Rota não encontrada";
            case METHOD_NOT_ALLOWED -> "Método HTTP não suportado para esta rota";
            case UNSUPPORTED_MEDIA_TYPE -> "Content-Type não suportado. Utilize application/json";
            case NOT_ACCEPTABLE -> "Formato de resposta não suportado. Utilize application/json";
            default -> status.getReasonPhrase();
        };
        return new ResponseEntity<>(body(status, mensagem, request, List.of()), headers, status);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String mensagem, WebRequest request,
            List<CampoInvalido> campos) {
        return ResponseEntity.status(status).body(body(status, mensagem, request, campos));
    }

    private ErrorResponse body(HttpStatus status, String mensagem, WebRequest request, List<CampoInvalido> campos) {
        String caminho = (request instanceof ServletWebRequest swr)
                ? swr.getRequest().getRequestURI()
                : null;
        return new ErrorResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(),
                mensagem, caminho, campos);
    }
}
