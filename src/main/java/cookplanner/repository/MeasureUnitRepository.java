package cookplanner.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cookplanner.domain.MeasureUnit;

public interface MeasureUnitRepository extends JpaRepository<MeasureUnit, Long> {

	Optional<MeasureUnit> findByName(String string);

}
