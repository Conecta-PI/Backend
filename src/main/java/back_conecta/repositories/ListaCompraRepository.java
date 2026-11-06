package back_conecta.repositories;

import back_conecta.entities.ListaCompra;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListaCompraRepository extends JpaRepository<ListaCompra, Long> {
    List<ListaCompra> findAllByOrderByDataCriacaoDesc();
}
