package com.ag.myfavoriterecipes.controller;

import com.ag.myfavoriterecipes.controller.converter.RecipeDtoConverter;
import com.ag.myfavoriterecipes.controller.dto.RecipeDto;
import com.ag.myfavoriterecipes.model.Recipe;
import com.ag.myfavoriterecipes.service.RecipeService;
import com.ag.myfavoriterecipes.service.exception.NoValidFilterException;
import com.ag.myfavoriterecipes.service.exception.RecipeNotFoundException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/recipes") // TODO naming
public class RecipeController {
	private final RecipeService recipeService;
	private final RecipeDtoConverter recipeDtoConverter;

	public RecipeController(RecipeService recipeService, RecipeDtoConverter recipeDtoConverter) {
		this.recipeService = recipeService;
		this.recipeDtoConverter = recipeDtoConverter;
	}

	@PostMapping
	@ApiResponses({
			@ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = RecipeDto.class))}),
			@ApiResponse(responseCode = "500", description = "Server error")})
	public ResponseEntity<RecipeDto> createRecipe(@RequestBody RecipeDto recipeDto) {
		try {
			Recipe savedRecipe = recipeService.addRecipe(recipeDtoConverter.fromDto(recipeDto));
			return ResponseEntity.status(HttpStatus.CREATED).body(recipeDtoConverter.toDto(savedRecipe));
		} catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PutMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = "200", content =
					{@Content(mediaType = "application/json", schema = @Schema(implementation = RecipeDto.class))}),
			@ApiResponse(responseCode = "404", description = "The recipe could not be found"),
			@ApiResponse(responseCode = "500", description = "Server error")})
	public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody RecipeDto recipeDto) {
		try {
			Recipe updatedRecipe = recipeService.updateRecipe(id, recipeDtoConverter.fromDto(recipeDto));

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
	@ApiResponses({
			@ApiResponse(responseCode = "204", content = {@Content(mediaType = "application/json")}),
			@ApiResponse(responseCode = "404", description = "The recipe could not be found"),
			@ApiResponse(responseCode = "500", description = "Server error")})
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

	//TODO pagination
	@GetMapping
	@ApiResponses({
			@ApiResponse(responseCode = "200", content =
					{@Content(mediaType = "application/json", schema = @Schema(implementation = RecipeDto.class))}),
			@ApiResponse(responseCode = "500", description = "Server error")})
	public ResponseEntity<List<RecipeDto>> getAllRecipes() {
		try {
			return ResponseEntity.ok(recipeService.getAllRecipes().stream().map(recipeDtoConverter::toDto).toList());
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/search")
	@ApiResponses({
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RecipeDto.class))}),
			@ApiResponse(responseCode = "500", description = "Server error")})
	public ResponseEntity<List<Recipe>> searchRecipes(@RequestParam(required = false) Boolean vegetarian,
													  @RequestParam(required = false) Integer servings,
													  @RequestParam(required = false) String includeIngredient,
													  @RequestParam(required = false) String excludeIngredient,
													  @RequestParam(required = false) String instructions) {
		try {
			return ResponseEntity.ok(
					recipeService.searchRecipes(vegetarian, servings, includeIngredient, excludeIngredient, instructions));
		}catch(NoValidFilterException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}

