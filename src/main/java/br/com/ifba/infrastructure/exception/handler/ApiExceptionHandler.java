package br.com.ifba.infrastructure.exception.handler;

import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.infrastructure.exception.dto.ErrorResponse;
import br.com.ifba.infrastructure.exception.dto.ValidationExceptionDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${server.error.include-exception:false}")
    private boolean printStackTrace;

    // --- 1. Tratamento Genérico (Erro 500) ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(
            Exception ex, WebRequest request) {
        log.error("Erro não tratado capturado", ex);
        return buildErrorMessage(ex, "Ocorreu um erro interno inesperado.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // --- 2. Regra de Negócio (Erro 422 - Unprocessable Entity) ---
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(
            BusinessException ex, WebRequest request) {
        return buildErrorMessage(ex, ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    // --- 3. Validação de DTO (@Valid) (Erro 400) ---
    @SuppressWarnings("NullableProblems")
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<String> fields = new ArrayList<>();
        List<String> messages = new ArrayList<>();

        // a) Erros de Campos Específicos (ex: email, cpf)
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fields.add(error.getField());
            messages.add(error.getDefaultMessage());
        }

        // b) Erros Globais (ex: @OuPontoOuEvento)
        // IMPORTANTE: Aqui capturamos o erro da anotação de classe!
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            fields.add(error.getObjectName()); // Retorna o nome do objeto (ex: reservaCadastroDTO)
            messages.add(error.getDefaultMessage());
        }

        ValidationExceptionDetails details = ValidationExceptionDetails.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .title("Campos Inválidos")
                .details("Verifique os campos listados abaixo.")
                .developerMessage(ex.getClass().getName())
                .fields(fields)
                .fieldsMessage(messages)
                .build();

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    // Método utilitário para construir ErrorResponse (Business e Exception genérica)
    private ResponseEntity<Object> buildErrorMessage(
            Exception exception,
            String mensagem,
            HttpStatus status) {

        ErrorResponse errorResponse = new ErrorResponse(status.value(), mensagem);

        // Se sua classe ErrorResponse não tiver setStacktrace ou não usar @Value, ajuste aqui
        if (this.printStackTrace) {
            errorResponse.setStacktrace(ExceptionUtils.getStackTrace(exception));
        }

        return ResponseEntity.status(status).body(errorResponse);
    }
}