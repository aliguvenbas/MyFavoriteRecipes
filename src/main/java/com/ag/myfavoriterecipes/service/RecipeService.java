package com.ag.myfavoriterecipes.service;

import com.ag.myfavoriterecipes.model.Recipe;
import com.ag.myfavoriterecipes.repository.RecipeRepository;
import com.ag.myfavoriterecipes.service.exception.RecipeNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RecipeService {
	private final RecipeRepository recipeRepository;

	public RecipeService(RecipeRepository recipeRepository) {
		this.recipeRepository = recipeRepository;
	}

	public Recipe addRecipe(Recipe recipe) {
		return recipeRepository.save(recipe);
	}

	public Recipe updateRecipe(Long id, Recipe recipeDetails) {
		return recipeRepository.findById(id)
				.map(recipe -> {
					recipe.setName(recipeDetails.getName());
					recipe.setVegetarian(recipeDetails.isVegetarian());
					recipe.setServings(recipeDetails.getServings());
					recipe.setInstructions(recipeDetails.getInstructions());
					recipe.setIngredients(recipeDetails.getIngredients());
					return recipeRepository.save(recipe);
				})
				.orElseThrow(() -> new RecipeNotFoundException(id));
	}

	public void deleteRecipe(Long id) {
		Recipe recipe = recipeRepository.findById(id)
				.orElseThrow(() -> new RecipeNotFoundException(id));
		recipeRepository.delete(recipe);
	}

	public List<Recipe> getAllRecipes() {
		return recipeRepository.findAll();
	}

	public List<Recipe> searchRecipes(Boolean isVegetarian, Integer servings, String includeIngredient, String excludeIngredient,
									  String instruction) {
		// Implement search logic using specifications or query methods
		return null;
	}
}
