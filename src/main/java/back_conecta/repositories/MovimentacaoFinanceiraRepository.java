package back_conecta.repositories;

import back_conecta.entities.MovimentacaoFinanceira;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoFinanceiraRepository extends JpaRepository<MovimentacaoFinanceira, Long> {
    List<MovimentacaoFinanceira> findAllByOrderByDataMovimentoDesc();
}