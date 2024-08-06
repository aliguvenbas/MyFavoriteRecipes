package com.ag.myfavoriterecipes.controller;

import com.ag.myfavoriterecipes.controller.converter.RecipeDtoConverter;
import com.ag.myfavoriterecipes.controller.dto.FilterDto;
import com.ag.myfavoriterecipes.controller.dto.RecipeDto;
import com.ag.myfavoriterecipes.model.Recipe;
import com.ag.myfavoriterecipes.service.RecipeService;
import com.ag.myfavoriterecipes.service.exception.NoValidFilterException;
import com.ag.myfavoriterecipes.service.exception.RecipeNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recipes")
@Validated
public class RecipeController {
	private final RecipeService recipeService;
	private final RecipeDtoConverter recipeDtoConverter;

	public RecipeController(RecipeService recipeService, RecipeDtoConverter recipeDtoConverter) {
		this.recipeService = recipeService;
		this.recipeDtoConverter = recipeDtoConverter;
	}

	@PostMapping
	@Validated
	@Operation(summary = "Create recipe")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "The recipe successfully created",
					content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RecipeDto.class))}),
			@ApiResponse(responseCode = "400", description = "The recipe can not be created, mandatory fields are empty", content = @Content()),
			@ApiResponse(responseCode = "500", description = "Server error", content = @Content())})
	public ResponseEntity<RecipeDto> createRecipe(@Valid @RequestBody RecipeDto recipeDto) {
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
	@Operation(summary = "Update recipe")
	@ApiResponses({
			@ApiResponse(responseCode = "200",
					description = "The recipe successfully updated",
					content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RecipeDto.class))}),
			@ApiResponse(responseCode = "400", description = "The recipe can not be updated, mandatory fields are empty", content = @Content()),
			@ApiResponse(responseCode = "404", description = "The recipe could not be found", content = @Content()),
			@ApiResponse(responseCode = "500", description = "Server error", content = @Content())})
	public ResponseEntity<RecipeDto> updateRecipe(@PathVariable Long id, @Valid @RequestBody RecipeDto recipeDto) {
		try {
			Recipe updatedRecipe = recipeService.updateRecipe(id, recipeDtoConverter.fromDto(recipeDto));

			return ResponseEntity.ok(recipeDtoConverter.toDto(updatedRecipe));
		} catch(RecipeNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete recipe")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "The recipe deleted succesfully"),
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

	@GetMapping
	@Operation(summary = "Get all recipe by pagination")
	@ApiResponses({
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RecipeDto.class))}),
			@ApiResponse(responseCode = "500", description = "Server error", content = @Content())})
	@Parameters({
			@Parameter(name = "page", description = "page number",
					in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
			@Parameter(name = "size", description = "page size",
					in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10"))})
	public ResponseEntity<Page<RecipeDto>> getAllRecipes(@PageableDefault(size = 10) Pageable pageable) {
		try {
			Page<RecipeDto> recipes = recipeService.getAllRecipes(pageable).map(recipeDtoConverter::toDto);
			return ResponseEntity.ok(recipes);
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/search")
	@Operation(summary = "Search the recipes according to the specified filters")
	@ApiResponses({
			@ApiResponse(responseCode = "200",
					content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RecipeDto.class))}),
			@ApiResponse(responseCode = "400", description = "There is no any valid filters", content = @Content()),
			@ApiResponse(responseCode = "500", description = "Server error", content = @Content())})
	@Parameters({
			@Parameter(name = "page", description = "page number",
					in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
			@Parameter(name = "size", description = "page size",
					in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10"))})
	public ResponseEntity<Page<RecipeDto>> searchRecipes(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
														 @RequestBody FilterDto filterDto) {
		try {
			return ResponseEntity.ok(
					recipeService.searchRecipes(pageable, filterDto.getVegetarian(), filterDto.getServings(),
									filterDto.getIncludeIngredient(),
									filterDto.getExcludeIngredient(), filterDto.getInstructions())
							.map(recipeDtoConverter::toDto)
			);
		} catch(NoValidFilterException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}

