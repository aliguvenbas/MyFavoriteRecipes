package com.ag.myfavoriterecipes.controller.converter;

import com.ag.myfavoriterecipes.controller.dto.RecipeDto;
import com.ag.myfavoriterecipes.model.Recipe;
import org.springframework.stereotype.Service;

@Service
public class RecipeDtoConverter {

	 public Recipe fromDto(RecipeDto recipeDto) {
		return new Recipe(recipeDto.getId(), recipeDto.getName(), recipeDto.isVegetarian(),
				recipeDto.getServings(), recipeDto.getInstructions(), recipeDto.getIngredients());
	}

	 public RecipeDto toDto(Recipe recipe) {
		return RecipeDto.builder()
				.id(recipe.getId())
				.name(recipe.getName())
				.vegetarian(recipe.isVegetarian())
				.servings(recipe.getServings())
				.ingredients(recipe.getIngredients())
				.instructions(recipe.getInstructions())
				.build();
	}
}
