package back_conecta.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyRecoveryCodeRequest(
        @NotBlank @Email String email,
        @NotBlank String codigo
) {}
