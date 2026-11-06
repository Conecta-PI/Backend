package back_conecta.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String nome,

        @NotBlank(message = "Telefone é obrigatório")
        @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
        String telefone,

        @Email(message = "E-mail inválido")
        @Size(max = 100, message = "E-mail deve ter no máximo 100 caracteres")
        String email,

        @Size(max = 1000, message = "Observações devem ter no máximo 1000 caracteres")
        String observacoes
) {
}
