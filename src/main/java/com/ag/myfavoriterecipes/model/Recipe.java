package com.ag.myfavoriterecipes.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.List;
import java.util.Set;

@Entity
public class Recipe { // TODO can we do it a record????
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private boolean isVegetarian;
	private int servings;
	private String instructions;

	@ElementCollection
	private Set<String> ingredients;

	// Getters and Setters

	public Recipe() {

	}

	public Recipe(String name, boolean isVegetarian, int servings, String instructions, Set<String> ingredients) {
		this.name = name;
		this.isVegetarian = isVegetarian;
		this.servings = servings;
		this.instructions = instructions;
		this.ingredients = ingredients;
	}


	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isVegetarian() {
		return isVegetarian;
	}

	public int getServings() {
		return servings;
	}

	public String getInstructions() {
		return instructions;
	}

	public Set<String> getIngredients() {
		return ingredients;
	}

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
