package com.ag.myfavoriterecipes.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ag.myfavoriterecipes.builders.RecipeTestBuilder;
import com.ag.myfavoriterecipes.model.Recipe;
import com.ag.myfavoriterecipes.repository.RecipeRepository;
import com.ag.myfavoriterecipes.service.exception.NoValidFilterException;
import com.ag.myfavoriterecipes.service.exception.RecipeNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public class RecipeServiceTest {
	private final RecipeRepository recipeRepository = mock(RecipeRepository.class);
	private final RecipeSpecGenerator recipeSpecGenerator = mock(RecipeSpecGenerator.class);
	private final RecipeService recipeService = new RecipeService(recipeRepository, recipeSpecGenerator);

	private Specification specification = mock(Specification.class);

	@Test
	public void shouldSaveRecipe() {
		Recipe recipe = new RecipeTestBuilder()
				.withName("Test Recipe")
				.withIsVegetarian(true)
				.withServings(2)
				.withInstructions("Test Instructions")
				.withIngredient(Set.of("Ingredient1", "Ingredient2"))
				.build();

		when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

		Recipe savedRecipe = recipeService.addRecipe(recipe);

		assertNotNull(savedRecipe);
		assertEquals("Test Recipe", savedRecipe.getName());
		assertTrue(savedRecipe.isVegetarian());
		assertEquals(2, savedRecipe.getServings());
		assertEquals("Test Instructions", savedRecipe.getInstructions());
		assertEquals(2, savedRecipe.getIngredients().size());
		assertEquals(Set.of("Ingredient1", "Ingredient2"), savedRecipe.getIngredients());

		verify(recipeRepository, times(1)).save(recipe);
	}

	@Test
	public void shouldThrowErrorIfRecipeHasIdDuringCreate() {
		Recipe recipe = new RecipeTestBuilder().withId(3L).build();

		assertThrows(IllegalArgumentException.class, () -> recipeService.addRecipe(recipe));

		verifyNoInteractions(recipeRepository);
	}

	@Test
	public void shouldUpdateRecipe() {
		Recipe existingRecipe = new RecipeTestBuilder()
				.withName("Test Recipe")
				.withIsVegetarian(true)
				.withServings(2)
				.withInstructions("Test Instructions")
				.withIngredient(Set.of("Ingredient1", "Ingredient2"))
				.build();

		Recipe updatedRecipeDetails = new RecipeTestBuilder()
				.withName("Updated Recipe")
				.withIsVegetarian(false)
				.withServings(2)
				.withInstructions("Updated Instructions")
				.withIngredient(Set.of("UpdatedIngredient1", "UpdatedIngredient2"))
				.build();

		when(recipeRepository.findById(1L)).thenReturn(Optional.of(existingRecipe));
		when(recipeRepository.save(any(Recipe.class))).thenReturn(existingRecipe);

		Recipe updatedRecipe = recipeService.updateRecipe(1L, updatedRecipeDetails);

		assertNotNull(updatedRecipe);
		assertEquals("Updated Recipe", updatedRecipe.getName());
		assertFalse(updatedRecipe.isVegetarian());
		assertEquals(2, updatedRecipe.getServings());
		assertEquals("Updated Instructions", updatedRecipe.getInstructions());
		assertEquals(Set.of("UpdatedIngredient1", "UpdatedIngredient2"), updatedRecipe.getIngredients());

		verify(recipeRepository, times(1)).findById(1L);
		verify(recipeRepository, times(1)).save(existingRecipe);
	}

	@Test
	public void shouldThrowExceptionIfRecipeWithGivenIdNotExistDuringUpdate() {
		when(recipeRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(RecipeNotFoundException.class, () -> recipeService.updateRecipe(1L, new RecipeTestBuilder().build()));
	}

	@Test
	public void shouldDeleteRecipe() {
		Recipe recipe = new RecipeTestBuilder().withId(1L).build();

		when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
		doNothing().when(recipeRepository).delete(recipe);

		recipeService.deleteRecipe(1L);

		verify(recipeRepository, times(1)).findById(1L);
		verify(recipeRepository, times(1)).delete(recipe);
	}

	@Test
	public void shouldThrowExceptionIfRecipeWithGivenIdNotExistDuringDelete() {
		when(recipeRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(RecipeNotFoundException.class, () -> recipeService.deleteRecipe(1L));
	}

	@Test
	void shouldGetAllRecipesWithPagination() {
		Recipe recipe1 = new RecipeTestBuilder().withName("Recipe 1").build();

		Recipe recipe2 = new RecipeTestBuilder().withName("Recipe 2").build();

		Recipe recipe3 = new RecipeTestBuilder().withName("Recipe 3").build();

		Page<Recipe> pagedRecipes = new PageImpl<>(List.of(recipe1, recipe2, recipe3));

		when(recipeRepository.findAll(any(Pageable.class))).thenReturn(pagedRecipes);

		Pageable pageable = PageRequest.of(0, 1);
		Page<Recipe> actualRecipes = recipeService.getAllRecipes(pageable);

		assertNotNull(actualRecipes);
		assertEquals(3, actualRecipes.getContent().size());

		verify(recipeRepository, times(1)).findAll(pageable);
	}

	@Test
	public void shouldSearchRecipes() {
		Recipe nonVegetarianRecipe = new RecipeTestBuilder().withIsVegetarian(false).withName("Non-V Recipe").build();

		when(recipeSpecGenerator.generateSpecs(eq(Boolean.FALSE), eq(null), eq(null), eq(null), eq(null))).thenReturn(specification);
		Page<Recipe> pagedRecipes = new PageImpl<>(List.of(nonVegetarianRecipe));
		when(recipeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pagedRecipes);

		Pageable pageable = PageRequest.of(0, 1);

		Page<Recipe> actualRecipes = recipeService.searchRecipes(pageable, Boolean.FALSE,
				null, null, null, null);

		assertEquals(1, actualRecipes.getContent().size());
		assertFalse(actualRecipes.getContent().get(0).isVegetarian());
		assertEquals("Non-V Recipe", actualRecipes.getContent().get(0).getName());

		verify(recipeSpecGenerator, times(1)).generateSpecs(eq(Boolean.FALSE), any(), any(), any(), any());
		verify(recipeRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
	}

	@Test
	public void shouldThrowExceptionIfThereIsNoAnyFilterDuringSearch() {
		assertThrows(NoValidFilterException.class, () -> recipeService.searchRecipes(null, null, null, null, null, null));
	}
}
