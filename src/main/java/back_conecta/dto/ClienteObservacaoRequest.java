package back_conecta.dto;

import jakarta.validation.constraints.Size;

public record ClienteObservacaoRequest(
        @Size(max = 1000, message = "Observação deve ter no máximo 1000 caracteres")
        String observacao
) {
}
