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
    private final PerfilImageStorage perfilImageStorage;

    public PerfilService(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository,
            VendaRepository vendaRepository,
            PerfilImageStorage perfilImageStorage
    ) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.vendaRepository = vendaRepository;
        this.perfilImageStorage = perfilImageStorage;
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

        if (request.getNome() != null) {
            if (request.getNome().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome é obrigatório.");
            }
            usuario.setNome(request.getNome().trim());
        }
        if (request.getNegocio() != null) {
            if (request.getNegocio().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome do negócio é obrigatório.");
            }
            usuario.setNomeNegocio(request.getNegocio().trim());
        }
        if (request.getTelefone() != null) {
            if (request.getTelefone().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone inválido.");
            }
            validarTelefone(request.getTelefone());
            usuario.setTelefone(request.getTelefone().trim());
        }
        if (request.getEmail() != null) {
            if (request.getEmail().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail inválido.");
            }
            String novoEmail = normalizarEmail(request.getEmail());
            if (!novoEmail.equalsIgnoreCase(usuario.getEmail())
                    && usuarioRepository.existsByEmailIgnoreCase(novoEmail)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já está em uso.");
            }
            usuario.setEmail(novoEmail);
        }
        if (request.getCnpj() != null) {
            if (request.getCnpj().isBlank()) {
                usuario.setCnpj(null);
            } else {
                usuario.setCnpj(validarFormatarCnpj(request.getCnpj()));
            }
        }
        if (request.getChavePix() != null) {
            usuario.setChavePix(request.getChavePix().isBlank() ? null : request.getChavePix().trim());
        }
        if (request.getPremium() != null) {
            usuario.setPremium(request.getPremium());
        }
        if (request.isFotoEnviada()) {
            String fotoAnterior = usuario.getFoto();
            String novaFoto = perfilImageStorage.salvarDataUrl(request.getFoto());
            usuario.setFoto(novaFoto);
            if (!java.util.Objects.equals(fotoAnterior, novaFoto)) {
                perfilImageStorage.removerAntiga(fotoAnterior);
            }
        }

        usuarioRepository.save(usuario);
        return buscar(usuario.getEmail());
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private void validarTelefone(String telefone) {
        String digitos = telefone.replaceAll("\\D", "");
        if (digitos.length() < 10 || digitos.length() > 15) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone inválido.");
        }
    }

    private String validarFormatarCnpj(String cnpj) {
        String digitos = cnpj.replaceAll("\\D", "");
        if (digitos.length() != 14 || digitos.matches("(\\d)\\1{13}") || !calcularDigitoCnpj(digitos)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CNPJ inválido.");
        }
        return digitos.substring(0, 2) + "." + digitos.substring(2, 5) + "."
                + digitos.substring(5, 8) + "/" + digitos.substring(8, 12)
                + "-" + digitos.substring(12);
    }

    private boolean calcularDigitoCnpj(String digitos) {
        int[] pesosPrimeiro = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesosSegundo = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int primeiro = calcularRestoCnpj(digitos.substring(0, 12), pesosPrimeiro);
        int segundo = calcularRestoCnpj(digitos.substring(0, 12) + primeiro, pesosSegundo);
        return digitos.endsWith("" + primeiro + segundo);
    }

    private int calcularRestoCnpj(String valor, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < valor.length(); i++) {
            soma += Character.digit(valor.charAt(i), 10) * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
