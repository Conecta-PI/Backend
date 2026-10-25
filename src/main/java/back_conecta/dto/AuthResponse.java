package back_conecta.dto;

public record AuthResponse(
        String tokenType,
        String accessToken,
        long expiresIn,
        UsuarioResponse usuario
) {
    public record UsuarioResponse(Long id, String nome, String email, String negocio) {}
}
