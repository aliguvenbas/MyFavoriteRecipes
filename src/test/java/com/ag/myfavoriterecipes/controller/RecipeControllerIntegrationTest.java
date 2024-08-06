package com.ag.myfavoriterecipes.controller;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ag.myfavoriterecipes.controller.dto.FilterDto;
import com.ag.myfavoriterecipes.controller.dto.RecipeDto;
import com.ag.myfavoriterecipes.model.Recipe;
import com.ag.myfavoriterecipes.repository.RecipeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RecipeControllerIntegrationTest {

	private static final String TEST_URL_BASE = "/api/v1/recipes";

	@Container
	public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:latest")
			.withDatabaseName("testdb")
			.withUsername("testuser")
			.withPassword("testpass");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private RecipeRepository recipeRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeAll
	static void beforeAll() {
		// Override the datasource URL, username, and password
		System.setProperty("spring.datasource.url", mysqlContainer.getJdbcUrl());
		System.setProperty("spring.datasource.username", mysqlContainer.getUsername());
		System.setProperty("spring.datasource.password", mysqlContainer.getPassword());
	}

	@BeforeEach
	void setUp() {
		recipeRepository.deleteAll();
	}

	@Test
	void shouldCreateRecipe() throws Exception {
		RecipeDto recipeDto = RecipeDto.builder()
				.name("Test Recipe")
				.vegetarian(true)
				.servings(4)
				.instructions("Test instructions")
				.ingredients(Set.of("ingredient1", "ingredient2"))
				.build();

		mockMvc.perform(post(TEST_URL_BASE)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(recipeDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", notNullValue()))
				.andExpect(jsonPath("$.name", is("Test Recipe")))
				.andExpect(jsonPath("$.vegetarian", is(true)))
				.andExpect(jsonPath("$.servings", is(4)))
				.andExpect(jsonPath("$.instructions", is("Test instructions")))
				.andExpect(jsonPath("$.ingredients", containsInAnyOrder("ingredient1", "ingredient2")));
	}

	@Test
	void shouldReturnBadRequestIfIdExistDuringCreateRecipe() throws Exception {
		RecipeDto recipeDto = RecipeDto.builder()
				.id(99L)
				.build();

		mockMvc.perform(post(TEST_URL_BASE)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(recipeDto)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void shouldNotCreateRecipeWithoutName() throws Exception {
		RecipeDto recipeDto = RecipeDto.builder().servings(4)
				.build();

		mockMvc.perform(post(TEST_URL_BASE)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(recipeDto)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void shouldNotCreateRecipeWithoutInstructions() throws Exception {
		RecipeDto recipeDto = RecipeDto.builder()
				.name("Some recipe")
				.servings(4)
				.build();

		mockMvc.perform(post(TEST_URL_BASE)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(recipeDto)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void shouldUpdateRecipe() throws Exception {
		Recipe existingRecipe = new Recipe();
		existingRecipe.setName("Existing Recipe");
		existingRecipe.setVegetarian(true);
		existingRecipe.setServings(4);
		existingRecipe.setInstructions("Existing instructions");
		existingRecipe.setIngredients(Set.of("ingredient1", "ingredient2"));
		long existingRecipeId = recipeRepository.save(existingRecipe).getId();

		RecipeDto updatedRecipe = RecipeDto.builder()
				.name("Updated Recipe")
				.vegetarian(false)
				.servings(2)
				.instructions("Updated instructions")
				.ingredients(Set.of("updatedIngredient1", "updatedIngredient2"))
				.build();

		mockMvc.perform(put(TEST_URL_BASE + "/" + existingRecipeId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updatedRecipe)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is("Updated Recipe")))
				.andExpect(jsonPath("$.vegetarian", is(false)))
				.andExpect(jsonPath("$.servings", is(2)))
				.andExpect(jsonPath("$.instructions", is("Updated instructions")))
				.andExpect(jsonPath("$.ingredients", containsInAnyOrder("updatedIngredient1", "updatedIngredient2")));
	}

	@Test
	void shouldReturnNotFoundIfTheGivenIdNotExistDuringUpdate() throws Exception {
		long notExistingRecipeId = 99L;
		RecipeDto recipeDto = RecipeDto.builder()
				.id(notExistingRecipeId)
				.name("Some recipe name")
				.instructions("some instructions")
				.build();

		mockMvc.perform(put(TEST_URL_BASE + "/" + notExistingRecipeId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(recipeDto)))
				.andExpect(status().isNotFound());
	}

	@Test
	void shouldDeleteRecipe() throws Exception {
		Recipe recipe = new Recipe();
		recipe.setName("Recipe will be deleted");
		recipe = recipeRepository.save(recipe);

		long existingRecipeId = recipe.getId();

		mockMvc.perform(delete(TEST_URL_BASE + "/" + existingRecipeId))
				.andExpect(status().isNoContent());

		assertFalse(recipeRepository.findById(existingRecipeId).isPresent());
	}

	@Test
	void shouldReturnNotFoundIfTheGivenIdNotExistDuringDelete() throws Exception {
		long notExistingRecipeId = 99L;

		mockMvc.perform(delete(TEST_URL_BASE + "/" + notExistingRecipeId))
				.andExpect(status().isNotFound());
	}

	@Test
	void shouldGetAllRecipesWithPagination() throws Exception {
		saveFourRecipes();

		mockMvc.perform(get(TEST_URL_BASE + "?page=0&size=3"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(3)));

		mockMvc.perform(get(TEST_URL_BASE + "?page=1&size=3"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(1)));
	}

	@Test
	void shouldSearchAllVegetarianRecipes() throws Exception {
		saveFourRecipes();

		mockMvc.perform(post(TEST_URL_BASE + "/search")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(FilterDto.builder().vegetarian(Boolean.TRUE).build())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(3)))
				.andExpect(jsonPath("$.content[0].name", anyOf(is("Vegetarian Sandwich"), is("Fried Potato"), is("Tomato soup"))))
				.andExpect(jsonPath("$.content[1].name", anyOf(is("Vegetarian Sandwich"), is("Fried Potato"), is("Tomato soup"))))
				.andExpect(jsonPath("$.content[2].name", anyOf(is("Vegetarian Sandwich"), is("Fried Potato"), is("Tomato soup"))));
	}

	@Test
	void shouldSearchRecipesWithIncludedIngredients() throws Exception {
		saveFourRecipes();

		mockMvc.perform(post(TEST_URL_BASE + "/search")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(FilterDto.builder().includeIngredient(List.of("potato")).build())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(2)))
				.andExpect(jsonPath("$.content[0].name", anyOf(is("Vegetarian Sandwich"), is("Fried Potato"))))
				.andExpect(jsonPath("$.content[1].name", anyOf(is("Vegetarian Sandwich"), is("Fried Potato"))));
	}

	@Test
	void shouldSearchRecipesWithVegetarianAndServingFilters() throws Exception {
		saveFourRecipes();

		mockMvc.perform(post(TEST_URL_BASE + "/search")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(FilterDto.builder().vegetarian(Boolean.TRUE).servings(2).build())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(1)))
				.andExpect(jsonPath("$.content[0].name", anyOf(is("Tomato soup"))));
	}

	@Test
	void shouldSearchRecipesWithExcludingIngredients() throws Exception {
		saveFourRecipes();

		mockMvc.perform(post(TEST_URL_BASE + "/search")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(FilterDto.builder().excludeIngredient(List.of("potato")).build())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(2)))
				.andExpect(jsonPath("$.content[0].name", anyOf(is("Barbeque"), is("Tomato soup"))))
				.andExpect(jsonPath("$.content[1].name", anyOf(is("Barbeque"), is("Tomato soup"))));
	}

	private void saveFourRecipes() {
		Recipe recipe1 = new Recipe();
		recipe1.setName("Vegetarian Sandwich");
		recipe1.setVegetarian(true);
		recipe1.setServings(4);
		recipe1.setInstructions("put everything in a bread");
		recipe1.setIngredients(Set.of("potato", "carrot", "tomato"));

		Recipe recipe2 = new Recipe();
		recipe2.setName("Barbeque");
		recipe2.setVegetarian(false);
		recipe2.setServings(2);
		recipe2.setInstructions("fry them all on the barbeque");
		recipe2.setIngredients(Set.of("chicken", "salmon"));

		Recipe recipe3 = new Recipe();
		recipe3.setName("Fried Potato");
		recipe3.setVegetarian(true);
		recipe3.setServings(4);
		recipe3.setInstructions("fry them all on the oven");
		recipe3.setIngredients(Set.of("potato", "paprika"));

		Recipe recipe4 = new Recipe();
		recipe4.setName("Tomato soup");
		recipe4.setVegetarian(true);
		recipe4.setServings(2);
		recipe4.setInstructions("slices the tomatos, put them to the saucepan");
		recipe4.setIngredients(Set.of("tomato"));

		recipeRepository.saveAll(Arrays.asList(recipe1, recipe2, recipe3, recipe4));

	}

	@Test
	void shouldReturnBadRequestIfNoFilterSpecifiedDuringSearchFunction() throws Exception {
		mockMvc.perform(post(TEST_URL_BASE + "/search")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(FilterDto.builder().build())))
				.andExpect(status().isBadRequest());
	}
}
