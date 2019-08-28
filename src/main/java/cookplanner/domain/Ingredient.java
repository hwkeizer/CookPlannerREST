package cookplanner.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties("recipe")
public class Ingredient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Recipe recipe;

	@OneToOne(fetch = FetchType.EAGER)
	private IngredientName name;
	
	private Float amount;
//	private boolean stock;
	
	@OneToOne(fetch = FetchType.EAGER)
	private MeasureUnit measureUnit;
	
	public Ingredient(IngredientName name, Float amount, MeasureUnit measureUnit) {
		this.name = name;
		this.amount = amount;
//		this.stock = stock;
		this.measureUnit = measureUnit;
	}	
	
	public Ingredient(Recipe recipe, IngredientName name, Float amount, MeasureUnit measureUnit) {
		this.recipe = recipe;
		this.name = name;
		this.amount = amount;
//		this.stock = stock;
		this.measureUnit = measureUnit;
	}	
}
