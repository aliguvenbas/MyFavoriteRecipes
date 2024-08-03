package com.ag.myfavoriterecipes.controller;

import com.ag.myfavoriterecipes.controller.dto.RecipeDto;
import com.ag.myfavoriterecipes.model.Recipe;
import com.ag.myfavoriterecipes.service.RecipeService;
import com.ag.myfavoriterecipes.service.exception.RecipeNotFoundException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
	private final RecipeService recipeService;

	public RecipeController(RecipeService recipeService) {
		this.recipeService = recipeService;
	}

	@PostMapping
	public ResponseEntity<Recipe> createRecipe(@RequestBody RecipeDto recipeDto) {
		Recipe recipe = new Recipe(recipeDto.getName(), recipeDto.isVegetarian(), recipeDto.getServings(), recipeDto.getInstructions(),
				recipeDto.getIngredients());//TODO converter
		Recipe savedRecipe = recipeService.addRecipe(recipe);
		return new ResponseEntity<>(savedRecipe, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody RecipeDto recipeDto) {
		try {
			Recipe recipe = new Recipe(recipeDto.getName(), recipeDto.isVegetarian(), recipeDto.getServings(), recipeDto.getInstructions(),
					recipeDto.getIngredients());
			Recipe updatedRecipe = recipeService.updateRecipe(id, recipe);

			// Also it can be return 200, because the vast majority of the applications except 200
			// if they make some validation or evaluation according to 200(which is not good)
			// it can be problematic
			return ResponseEntity.ok(updatedRecipe);
		} catch(RecipeNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteRecipe(@PathVariable Long id) {
		try {
			recipeService.deleteRecipe(id);

			// Also it can be return 200, because vast majority of the applications except 200
			// if they make some validation or evaluation according to 200(which is not good)
			// it can be problematic
			return ResponseEntity.noContent().build();
		} catch(RecipeNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping
	public ResponseEntity<List<Recipe>> getAllRecipes() {
		return ResponseEntity.ok(recipeService.getAllRecipes());
	}

	@GetMapping("/search")
	public ResponseEntity<List<Recipe>> searchRecipes(@RequestParam(required = false) Boolean isVegetarian,
													  @RequestParam(required = false) Integer servings,
													  @RequestParam(required = false) String includeIngredient,
													  @RequestParam(required = false) String excludeIngredient,
													  @RequestParam(required = false) String instruction) {
		return ResponseEntity.ok(recipeService.searchRecipes(isVegetarian, servings, includeIngredient, excludeIngredient, instruction));
	}
}

