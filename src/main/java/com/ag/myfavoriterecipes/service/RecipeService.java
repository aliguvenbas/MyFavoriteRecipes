package com.ag.myfavoriterecipes.service;

import static com.ag.myfavoriterecipes.repository.RecipeSpecs.includedIngredients;
import static com.ag.myfavoriterecipes.repository.RecipeSpecs.instructionsLike;
import static com.ag.myfavoriterecipes.repository.RecipeSpecs.servingsTo;
import static com.ag.myfavoriterecipes.repository.RecipeSpecs.vegetarian;

import com.ag.myfavoriterecipes.model.Recipe;
import com.ag.myfavoriterecipes.repository.RecipeRepository;
import com.ag.myfavoriterecipes.service.exception.RecipeNotFoundException;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
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

	public List<Recipe> searchRecipes(Boolean isVegetarian, Integer servings, String includeIngredient,
									  String excludeIngredient, String instruction) {
		Specification<Recipe> filters =
				Specification.where(isVegetarian == null ? null : vegetarian(isVegetarian))
						.and(servings == null ? null : servingsTo(servings))
						.and(StringUtils.isEmpty(includeIngredient) ? null : includedIngredients(List.of(includeIngredient)))
//				.and(StringUtils.isEmpty(excludeIngredient) ? null : inCity(cities)) // TODO
						.and(StringUtils.isEmpty(instruction) ? null : instructionsLike(instruction));

		return recipeRepository.findAll(filters);

	}
}
