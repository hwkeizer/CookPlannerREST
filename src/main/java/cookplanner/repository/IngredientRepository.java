package cookplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cookplanner.domain.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

}
