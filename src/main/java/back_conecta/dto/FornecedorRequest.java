package back_conecta.dto;

import jakarta.validation.constraints.Email;

public record FornecedorRequest(
        String nome,
        String categoria,
        String contato,
        String telefone,
        @Email(message = "E-mail do fornecedor inválido") String email,
        String observacao,
        Boolean ativo
) {
}
