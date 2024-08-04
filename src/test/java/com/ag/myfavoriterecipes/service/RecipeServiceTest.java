package com.ag.myfavoriterecipes.service;

import static org.junit.jupiter.api.Assertions.*;

import com.ag.myfavoriterecipes.model.Recipe;
import com.ag.myfavoriterecipes.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RecipeServiceTest {

	@InjectMocks
	private RecipeService recipeService;

	@Mock
	private RecipeRepository recipeRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void shouldAddRecipe() {
		Recipe recipe = new Recipe();
		recipe.setName("Test Recipe");

		when(recipeRepository.save(recipe)).thenReturn(recipe);

		Recipe savedRecipe = recipeService.addRecipe(recipe);

		assertNotNull(savedRecipe);
		assertEquals("Test Recipe", savedRecipe.getName());
		verify(recipeRepository, times(1)).save(recipe);
	}

	@Test
	void shouldUpdateRecipe() throws Exception {
		Recipe recipe = new Recipe();
//		recipe.setId(1L);
		recipe.setName("Updated Recipe");

		when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
		when(recipeRepository.save(recipe)).thenReturn(recipe);

		Recipe updatedRecipe = recipeService.updateRecipe(1L, recipe);

		assertNotNull(updatedRecipe);
		assertEquals("Updated Recipe", updatedRecipe.getName());
		verify(recipeRepository, times(1)).findById(1L);
		verify(recipeRepository, times(1)).save(recipe);
	}

	@Test
	void shouldDeleteRecipe() throws Exception {
		Recipe recipe = new Recipe();
//		recipe.setId(1L);

		when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

		recipeService.deleteRecipe(1L);

		verify(recipeRepository, times(1)).findById(1L);
		verify(recipeRepository, times(1)).delete(recipe);
	}

	@Test
	void shouldGetAllRecipes() {
		Recipe recipe1 = new Recipe();
		Recipe recipe2 = new Recipe();

		when(recipeRepository.findAll()).thenReturn(Arrays.asList(recipe1, recipe2));

		assertEquals(2, recipeService.getAllRecipes().size());
		verify(recipeRepository, times(1)).findAll();
	}
}
