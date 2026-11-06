package back_conecta.dto;

import jakarta.validation.constraints.NotBlank;

public record FornecedorRequest(
        @NotBlank String nome,
        String categoria,
        String contato,
        String telefone,
        String email,
        String observacao,
        Boolean ativo
) {
}
