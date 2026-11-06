package back_conecta.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record PerfilUpdateRequest(
        @Size(max = 100) String nome,
        @Size(max = 150) String negocio,
        @Size(max = 20) String telefone,
        @Email @Size(max = 100) String email,
        @Size(max = 18) String cnpj,
        @Size(max = 150) String chavePix,
        Boolean premium
) {}
