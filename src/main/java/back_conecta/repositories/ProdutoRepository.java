package back_conecta.repositories;

import back_conecta.entities.Produto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findAllByAtivoTrueOrderByNomeProdutoAsc();
    Optional<Produto> findFirstByNomeProdutoIgnoreCase(String nomeProduto);
}