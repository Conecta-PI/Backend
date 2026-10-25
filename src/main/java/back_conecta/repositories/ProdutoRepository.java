package back_conecta.repositories;

import back_conecta.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findAllByAtivoTrueOrderByNomeProdutoAsc();
    Optional<Produto> findFirstByNomeProdutoIgnoreCase(String nomeProduto);
}