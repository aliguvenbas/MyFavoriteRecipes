package com.ag.myfavoriterecipes.builders;

import com.ag.myfavoriterecipes.model.Recipe;
import java.util.HashSet;
import java.util.Set;

public class RecipeTestBuilder {
	private Long id;
	private String name;
	private boolean isVegetarian;
	private int servings;
	private String instructions;
	private Set<String> ingredients = new HashSet<>();

	public RecipeTestBuilder withId(Long id) {
		this.id = id;
		return this;
	}

	public RecipeTestBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public RecipeTestBuilder withIsVegetarian(boolean isVegetarian) {
		this.isVegetarian = isVegetarian;
		return this;
	}

	public RecipeTestBuilder withServings(int servings) {
		this.servings = servings;
		return this;
	}

	public RecipeTestBuilder withInstructions(String instructions) {
		this.instructions = instructions;
		return this;
	}

	public RecipeTestBuilder withIngredient(Set ingredient) {
		this.ingredients.addAll(ingredient);
		return this;
	}

	public Recipe build() {
		return  new Recipe(id, name, isVegetarian, servings, instructions, ingredients);
	}
}

