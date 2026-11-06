package back_conecta.services;

import back_conecta.dto.AuthResponse;
import back_conecta.dto.CadastroUsuarioRequest;
import back_conecta.dto.ForgotPasswordRequest;
import back_conecta.dto.LoginRequest;
import back_conecta.dto.MensagemResponse;
import back_conecta.dto.RecoveryVerificationResponse;
import back_conecta.dto.ResetPasswordRequest;
import back_conecta.dto.VerifyRecoveryCodeRequest;
import back_conecta.entities.Usuario;
import back_conecta.repositories.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private static final long TOKEN_EXPIRATION_SECONDS = ChronoUnit.HOURS.getDuration().toSeconds() * 12;
    private static final Map<String, String> CODIGOS_RECUPERACAO = new ConcurrentHashMap<>();
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    @Transactional
    public synchronized AuthResponse cadastrar(CadastroUsuarioRequest request) {
        if (usuarioRepository.count() > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A conta do responsável pelo negócio já foi criada. Entre com o e-mail cadastrado."
            );
        }

        String email = normalizarEmail(request.email());
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já possui uma conta.");
        }
        validarTamanhoSenha(request.senha());

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome().trim());
        usuario.setEmail(email);
        usuario.setTelefone(request.telefone().trim());
        usuario.setNomeNegocio(request.negocio().trim());
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        usuario.setPremium(false);
        usuario.setCnpj(null);
        usuario.setChavePix(null);

        return criarResposta(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public AuthResponse autenticar(LoginRequest request) {
        if (request.senha().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw credenciaisInvalidas();
        }
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(normalizarEmail(request.email()))
                .orElseThrow(AuthService::credenciaisInvalidas);

        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw credenciaisInvalidas();
        }

        return criarResposta(usuario);
    }

    @Transactional(readOnly = true)
    public MensagemResponse solicitarRecuperacao(ForgotPasswordRequest request) {
        String email = normalizarEmail(request.email());
        usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));

        String codigo = String.format("%06d", RANDOM.nextInt(1000000));
        CODIGOS_RECUPERACAO.put(email, codigo);
        return new MensagemResponse("Código de recuperação enviado para o e-mail informado. (Código de teste: " + codigo + ")");
    }

    @Transactional(readOnly = true)
    public RecoveryVerificationResponse verificarCodigoRecuperacao(VerifyRecoveryCodeRequest request) {
        String email = normalizarEmail(request.email());
        String codigoEsperado = CODIGOS_RECUPERACAO.get(email);
        boolean valido = codigoEsperado != null && codigoEsperado.equals(request.codigo().trim());
        return new RecoveryVerificationResponse(valido);
    }

    @Transactional
    public MensagemResponse redefinirSenha(ResetPasswordRequest request) {
        String email = normalizarEmail(request.email());
        String codigoEsperado = CODIGOS_RECUPERACAO.get(email);
        if (codigoEsperado == null || !codigoEsperado.equals(request.codigo().trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código de recuperação inválido ou expirado.");
        }

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        validarTamanhoSenha(request.novaSenha());
        usuario.setSenhaHash(passwordEncoder.encode(request.novaSenha()));
        usuarioRepository.save(usuario);
        CODIGOS_RECUPERACAO.remove(email);
        return new MensagemResponse("Senha redefinida com sucesso.");
    }

    private AuthResponse criarResposta(Usuario usuario) {
        Instant agora = Instant.now();
        var claims = JwtClaimsSet.builder()
                .issuer("conecta-api")
                .issuedAt(agora)
                .expiresAt(agora.plusSeconds(TOKEN_EXPIRATION_SECONDS))
                .subject(usuario.getEmail())
                .claim("uid", usuario.getIdUsuario())
                .build();
        var header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new AuthResponse(
                "Bearer",
                token,
                TOKEN_EXPIRATION_SECONDS,
                new AuthResponse.UsuarioResponse(
                        usuario.getIdUsuario(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getNomeNegocio()
                )
        );
    }

    private void validarTamanhoSenha(String senha) {
        if (senha == null || senha.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A senha deve ter no máximo 72 bytes em UTF-8."
            );
        }
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static ResponseStatusException credenciaisInvalidas() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
    }
}
