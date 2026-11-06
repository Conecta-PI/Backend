package back_conecta.repositories;

import back_conecta.entities.Encomenda;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EncomendaRepository extends JpaRepository<Encomenda, Long> {
    List<Encomenda> findAllByOrderByDataPedidoDesc();
}
