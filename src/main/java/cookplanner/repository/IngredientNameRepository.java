package cookplanner.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cookplanner.domain.IngredientName;

public interface IngredientNameRepository extends JpaRepository<IngredientName, Long>{

	Optional<IngredientName> findByName(String string);

}
