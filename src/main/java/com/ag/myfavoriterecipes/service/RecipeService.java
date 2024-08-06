package com.ag.myfavoriterecipes.service;

import com.ag.myfavoriterecipes.model.Recipe;
import com.ag.myfavoriterecipes.repository.RecipeRepository;
import com.ag.myfavoriterecipes.service.exception.NoValidFilterException;
import com.ag.myfavoriterecipes.service.exception.RecipeNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class RecipeService {
	private final RecipeRepository recipeRepository;
	private final RecipeSpecGenerator recipeSpecGenerator;

	public RecipeService(RecipeRepository recipeRepository, RecipeSpecGenerator recipeSpecGenerator) {
		this.recipeRepository = recipeRepository;
		this.recipeSpecGenerator = recipeSpecGenerator;
	}

	public Recipe addRecipe(Recipe recipe) {
		if(recipe.getId() != null) {
			throw new IllegalArgumentException("Recipe with an id can not be created");
		}

		return recipeRepository.save(recipe);
	}

	public synchronized Recipe updateRecipe(Long id, Recipe recipeDetails) {
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

	public Page<Recipe> getAllRecipes(Pageable pageable) {
		return recipeRepository.findAll(pageable);
	}

	public Page<Recipe> searchRecipes(Pageable pageable, Boolean isVegetarian, Integer servings, String includeIngredient,
									  String excludeIngredient, String instruction) {
		boolean noValidFilter = isVegetarian == null
				&& servings == null
				&& includeIngredient == null
				&& excludeIngredient == null
				&& instruction == null;

		if(noValidFilter) {
			throw new NoValidFilterException();
		}
		Specification<Recipe> filters =
				recipeSpecGenerator.generateSpecs(isVegetarian, servings, includeIngredient, excludeIngredient, instruction);

		return recipeRepository.findAll(filters, pageable);
	}
}
