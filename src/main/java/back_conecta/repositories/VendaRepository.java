package back_conecta.repositories;

import back_conecta.entities.Venda;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findAllByOrderByDataVendaDesc();
    List<Venda> findAllByClienteIdClienteOrderByDataVendaDesc(Long clienteId);
}