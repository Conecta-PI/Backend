package back_conecta.repositories;

import back_conecta.entities.DashboardDemo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardDemoRepository extends JpaRepository<DashboardDemo, Long> {
    Optional<DashboardDemo> findByCodigo(String codigo);
}
