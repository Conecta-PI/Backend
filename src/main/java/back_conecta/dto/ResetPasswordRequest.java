package back_conecta.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank @Email String email,
        @NotBlank String codigo,
        @NotBlank @Size(min = 8, max = 72) String novaSenha
) {}
