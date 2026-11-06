package back_conecta.controllers;

import back_conecta.dto.PerfilResponse;
import back_conecta.dto.PerfilUpdateRequest;
import back_conecta.services.PerfilService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/perfil")
public class PerfilController {
    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping
    public PerfilResponse buscar(@AuthenticationPrincipal Jwt token) {
        return perfilService.buscar(token.getSubject());
    }

    @PatchMapping
    public PerfilResponse atualizar(
            @AuthenticationPrincipal Jwt token,
            @Valid @RequestBody PerfilUpdateRequest request
    ) {
        return perfilService.atualizar(token.getSubject(), request);
    }
}
