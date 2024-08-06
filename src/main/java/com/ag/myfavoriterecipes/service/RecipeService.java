package com.ag.myfavoriterecipes.service;

import com.ag.myfavoriterecipes.model.Recipe;
import com.ag.myfavoriterecipes.repository.RecipeRepository;
import com.ag.myfavoriterecipes.service.exception.NoValidFilterException;
import com.ag.myfavoriterecipes.service.exception.RecipeNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional
public class RecipeService {
	private static final Logger LOGGER = LoggerFactory.getLogger(RecipeService.class);

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
		Recipe savedRecipe = recipeRepository.save(recipe);
		LOGGER.info("The recipe {} is create", savedRecipe.getName());

		return savedRecipe;
	}

	public synchronized Recipe updateRecipe(Long id, Recipe recipeDetails) {
		return recipeRepository.findById(id)
				.map(recipe -> {
					recipe.setName(recipeDetails.getName());
					recipe.setVegetarian(recipeDetails.isVegetarian());
					recipe.setServings(recipeDetails.getServings());
					recipe.setInstructions(recipeDetails.getInstructions());
					recipe.setIngredients(recipeDetails.getIngredients());
					Recipe updatedRecipe = recipeRepository.save(recipe);

					LOGGER.info("The recipe {} is updated", updatedRecipe.getName());
					return updatedRecipe;
				})
				.orElseThrow(() -> new RecipeNotFoundException(id));
	}

	public void deleteRecipe(Long id) {
		Recipe recipe = recipeRepository.findById(id)
				.orElseThrow(() -> new RecipeNotFoundException(id));
		recipeRepository.delete(recipe);

		LOGGER.info("The recipe {} is deleted", id);

	}

	public Page<Recipe> getAllRecipes(Pageable pageable) {
		return recipeRepository.findAll(pageable);
	}

	public Page<Recipe> searchRecipes(Pageable pageable, Boolean isVegetarian, Integer servings, List<String> includeIngredient,
									  List<String> excludeIngredient, String instruction) {
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
