package cookplanner.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.EAGER)
	private IngredientName name;
	
	private Float amount;
	private boolean stock;
	
	@OneToOne(fetch = FetchType.EAGER)
	private MeasureUnit measureUnit;
	
	public Ingredient(IngredientName name, Float amount, boolean stock, MeasureUnit measureUnit) {
		this.name = name;
		this.amount = amount;
		this.stock = stock;
		this.measureUnit = measureUnit;
	}
}
