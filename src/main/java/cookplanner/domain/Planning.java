package cookplanner.domain;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Planning implements Comparable<Planning> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private LocalDate date;
	
	@ManyToOne
	private Recipe recipe;
	
	private String name;
	private Integer servings;	
	private boolean onShoppingList;

	@Override
	public int compareTo(Planning other) {
		return this.getDate().compareTo(other.getDate());
	}
}
