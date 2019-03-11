package cookplanner.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Recipe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	private String description;

	@Lob
	private String notes;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "recipe_id")
	private Set<Ingredient> ingredients = new HashSet<>();

	private String image;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
	private RecipeType recipeType;

	@ManyToMany
	@JoinTable(name = "recipe_tag", joinColumns = @JoinColumn(name = "recipe_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private Set<Tag> tags = new HashSet<>();

	private Integer preparationTime;
	private Integer cookTime;
	private Integer servings;

	@Lob
	private String preparations;
	
	@Lob
	private String directions;

	private Integer rating;
	
	public Recipe(String name) {
		this.name = name;
	}
	
	public void addIngredient(Ingredient ingredient) {
//		ingredient.setRecipe(this); // Set both sides of the relation
		ingredients.add(ingredient);
	}
	
	public void removeIngredient(Ingredient ingredient) {
//		ingredient.setRecipe(null);
		ingredients.remove(ingredient);
		
	}
}