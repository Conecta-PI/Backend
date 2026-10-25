package back_conecta.repositories;

import back_conecta.entities.DashboardRecebidoDemo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardRecebidoDemoRepository extends JpaRepository<DashboardRecebidoDemo, Long> {
    List<DashboardRecebidoDemo> findAllByDashboardIdOrderById(Long dashboardId);
}
