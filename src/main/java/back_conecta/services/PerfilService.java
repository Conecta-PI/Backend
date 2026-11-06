package back_conecta.services;

import back_conecta.dto.PerfilResponse;
import back_conecta.dto.PerfilUpdateRequest;
import back_conecta.entities.Usuario;
import back_conecta.repositories.ClienteRepository;
import back_conecta.repositories.ProdutoRepository;
import back_conecta.repositories.UsuarioRepository;
import back_conecta.repositories.VendaRepository;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PerfilService {
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final VendaRepository vendaRepository;

    public PerfilService(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository,
            VendaRepository vendaRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.vendaRepository = vendaRepository;
    }

    @Transactional(readOnly = true)
    public PerfilResponse buscar(String email) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(normalizarEmail(email))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "A conta autenticada não foi encontrada."
                ));

        return PerfilResponse.from(
                usuario,
                vendaRepository.count(),
                clienteRepository.count(),
                produtoRepository.count()
        );
    }

    @Transactional
    public PerfilResponse atualizar(String email, PerfilUpdateRequest request) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(normalizarEmail(email))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "A conta autenticada não foi encontrada."
                ));

        if (request.nome() != null && !request.nome().isBlank()) {
            usuario.setNome(request.nome().trim());
        }
        if (request.negocio() != null && !request.negocio().isBlank()) {
            usuario.setNomeNegocio(request.negocio().trim());
        }
        if (request.telefone() != null && !request.telefone().isBlank()) {
            usuario.setTelefone(request.telefone().trim());
        }
        if (request.email() != null && !request.email().isBlank()) {
            String novoEmail = normalizarEmail(request.email());
            if (!novoEmail.equalsIgnoreCase(usuario.getEmail())
                    && usuarioRepository.existsByEmailIgnoreCase(novoEmail)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já está em uso.");
            }
            usuario.setEmail(novoEmail);
        }
        if (request.cnpj() != null) {
            usuario.setCnpj(request.cnpj().isBlank() ? null : request.cnpj().trim());
        }
        if (request.chavePix() != null) {
            usuario.setChavePix(request.chavePix().isBlank() ? null : request.chavePix().trim());
        }
        if (request.premium() != null) {
            usuario.setPremium(request.premium());
        }

        usuarioRepository.save(usuario);
        return buscar(usuario.getEmail());
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
