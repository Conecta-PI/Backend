package back_conecta.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroUsuarioRequest(
        @NotBlank @Size(max = 100) String nome,
        @NotBlank @Email @Size(max = 100) String email,
        @NotBlank @Size(min = 10, max = 20) String telefone,
        @NotBlank @Size(min = 8, max = 72) String senha,
        @NotBlank @Size(max = 150) String negocio
) {}
