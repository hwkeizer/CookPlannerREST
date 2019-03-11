package cookplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cookplanner.domain.Planning;

public interface PlanningRepository extends JpaRepository<Planning, Long> {

}
