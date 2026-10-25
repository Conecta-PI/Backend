package back_conecta.repositories;

import back_conecta.entities.DashboardAlertaDemo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardAlertaDemoRepository extends JpaRepository<DashboardAlertaDemo, Long> {
    List<DashboardAlertaDemo> findAllByDashboardIdOrderById(Long dashboardId);
}
