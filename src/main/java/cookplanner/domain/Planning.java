package cookplanner.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The planning class contains the recipe (or '- no recipe planned -') for a single day. 
 * There can only be one planning for each day on the planBoard so date is a unique field.
 * equals, hashCode and compareTo methods will all only use the date field.
 */
@Entity
@Data
@NoArgsConstructor
public class Planning implements Comparable<Planning> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique=true)
	private LocalDate date;
	
	@ManyToOne
	private Recipe recipe;
	
	@Column(length = 60)
	private String name = "- Geen recept gepland -";
	
	private Integer servings;	
	private boolean onShoppingList = true;
	
	public Planning(LocalDate date) {
		this.date = date;
	}
	
	public void setRecipe(Recipe recipe) {
		if (recipe != null) {
			this.recipe = recipe;
			this.name = recipe.getName();
			this.servings = recipe.getServings();
		}
	}
	
	@Override
	public int compareTo(Planning other) {
		return this.getDate().compareTo(other.getDate());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Planning other = (Planning) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		return result;
	}
	
	
}
