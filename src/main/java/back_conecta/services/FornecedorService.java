package back_conecta.services;

import back_conecta.dto.FornecedorRequest;
import back_conecta.dto.FornecedorResponse;
import back_conecta.entities.Fornecedor;
import back_conecta.repositories.FornecedorRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FornecedorService {
    private final FornecedorRepository fornecedorRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    @Transactional(readOnly = true)
    public List<FornecedorResponse> listar() {
        return fornecedorRepository.findAllByOrderByNomeAsc().stream()
                .map(FornecedorResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public FornecedorResponse buscar(Long id) {
        return FornecedorResponse.from(
                fornecedorRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado"))
        );
    }

    @Transactional
    public FornecedorResponse criar(FornecedorRequest request) {
        if (request.nome() == null || request.nome().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome do fornecedor é obrigatório");
        }
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setNome(request.nome().trim());
        fornecedor.setCategoria(request.categoria());
        fornecedor.setContato(request.contato());
        fornecedor.setTelefone(request.telefone());
        fornecedor.setEmail(request.email());
        fornecedor.setObservacao(request.observacao());
        fornecedor.setAtivo(request.ativo() == null || request.ativo());

        return FornecedorResponse.from(fornecedorRepository.save(fornecedor));
    }

    @Transactional
    public FornecedorResponse atualizar(Long id, FornecedorRequest request) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado"));

        if (request.nome() != null) {
            if (request.nome().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome do fornecedor é obrigatório");
            }
            fornecedor.setNome(request.nome().trim());
        }
        if (request.categoria() != null) {
            fornecedor.setCategoria(request.categoria());
        }
        if (request.contato() != null) {
            fornecedor.setContato(request.contato());
        }
        if (request.telefone() != null) {
            fornecedor.setTelefone(request.telefone());
        }
        if (request.email() != null) {
            fornecedor.setEmail(request.email());
        }
        if (request.observacao() != null) {
            fornecedor.setObservacao(request.observacao());
        }
        if (request.ativo() != null) {
            fornecedor.setAtivo(request.ativo());
        }

        return FornecedorResponse.from(fornecedorRepository.save(fornecedor));
    }

    @Transactional
    public void excluir(Long id) {
        if (!fornecedorRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado");
        }
        fornecedorRepository.deleteById(id);
    }
}
