package cookplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cookplanner.domain.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

}
