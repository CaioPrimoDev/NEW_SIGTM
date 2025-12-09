package br.com.ifba.pontoturistico.dto;

import br.com.ifba.endereco.dto.EnderecoCadastroDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PontoTuristicoDTO {

    // --- Dados do ItemTuristico ---
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
    private String descricao;

    // int primitivo não aceita null, então @NotNull é redundante, mas @Min/@Max são essenciais
    @Min(value = 1, message = "O nível de acessibilidade deve ser no mínimo 1")
    @Max(value = 5, message = "O nível de acessibilidade deve ser no máximo 5")
    private int nivelAcessibilidade;

    // --- Dados específicos ---

    @NotBlank(message = "Horário de abertura é obrigatório")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Formato de hora inválido. Use HH:mm")
    private String horarioAbertura;

    @NotBlank(message = "Horário de fechamento é obrigatório")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Formato de hora inválido. Use HH:mm")
    private String horarioFechamento;

    // --- Endereço Aninhado ---
    @NotNull(message = "O endereço é obrigatório")
    @Valid // <--- ESSENCIAL: Diz ao Spring para entrar no objeto e validar os campos dele
    private EnderecoCadastroDTO endereco;
}
