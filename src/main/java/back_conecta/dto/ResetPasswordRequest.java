package back_conecta.dto;

import back_conecta.validation.SenhaValidator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank @Email String email,
        @NotBlank String codigo,
        @NotBlank
        @Size(min = 8, max = 72)
        @Pattern(
                regexp = SenhaValidator.REGEX,
                message = "A senha deve ter pelo menos 8 caracteres, incluindo letra maiúscula, letra minúscula e número."
        )
        String novaSenha
) {}
