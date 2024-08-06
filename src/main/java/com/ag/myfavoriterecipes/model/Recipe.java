package com.ag.myfavoriterecipes.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@Getter
@AllArgsConstructor
public class Recipe {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private boolean isVegetarian;
	private int servings;
	private String instructions;

	@ElementCollection
	private Set<String> ingredients;

	public Recipe() {

	}

	// Id can be set only in creation

	public void setName(String name) {
		this.name = name;
	}

	public void setVegetarian(boolean vegetarian) {
		isVegetarian = vegetarian;
	}

	public void setServings(int servings) {
		this.servings = servings;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public void setIngredients(Set<String> ingredients) {
		this.ingredients = ingredients;
	}
}
