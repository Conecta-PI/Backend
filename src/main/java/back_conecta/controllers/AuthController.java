package back_conecta.controllers;

import back_conecta.dto.AuthResponse;
import back_conecta.dto.CadastroUsuarioRequest;
import back_conecta.dto.LoginRequest;
import back_conecta.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> cadastrar(@Valid @RequestBody CadastroUsuarioRequest request) {
        AuthResponse resposta = authService.cadastrar(request);
        return ResponseEntity.status(201).body(resposta);
    }

    @PostMapping("/login")
    public AuthResponse autenticar(@Valid @RequestBody LoginRequest request) {
        return authService.autenticar(request);
    }
}
