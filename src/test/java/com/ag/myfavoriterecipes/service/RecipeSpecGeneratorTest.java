package com.ag.myfavoriterecipes.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ag.myfavoriterecipes.model.Recipe;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

public class RecipeSpecGeneratorTest {

	private final RecipeSpecGenerator recipeSpecGenerator = new RecipeSpecGenerator();
	;

	private Root<Recipe> root = mock(Root.class);
	private CriteriaQuery<?> query = mock(CriteriaQuery.class);
	private CriteriaBuilder builder = mock(CriteriaBuilder.class);
	private Predicate predicate = mock(Predicate.class);
	private Join<Object, Object> join = mock(Join.class);

	@Test
	public void shouldGenerateSpecsForVegetarianFilter() {
		Specification<Recipe> spec = recipeSpecGenerator.generateSpecs(Boolean.TRUE, null, null, null, null);
		when(builder.equal(root.get("isVegetarian"), true)).thenReturn(predicate);

		Predicate result = spec.toPredicate(root, query, builder);
		assertNotNull(result);

		verify(builder).equal(root.get("isVegetarian"), true);
	}

	@Test
	public void shouldGenerateSpecsForServingFilter() {
		Specification<Recipe> spec = recipeSpecGenerator.generateSpecs(null, 4, null, null, null);
		when(builder.equal(root.get("servings"), 4)).thenReturn(predicate);

		Predicate result = spec.toPredicate(root, query, builder);
		assertNotNull(result);

		verify(builder).equal(root.get("servings"), 4);
	}

	@Test
	public void shouldGenerateSpecsForIncludeIngredientFilter() {
		Specification<Recipe> spec = recipeSpecGenerator.generateSpecs(null, null, "Tomato", null, null);
		when(root.join("ingredients")).thenReturn(join);
		when(builder.equal(join, "Tomato")).thenReturn(predicate);
		when(builder.or(predicate)).thenReturn(predicate);

		Predicate result = spec.toPredicate(root, query, builder);
		assertNotNull(result);
		verify(root).join("ingredients");
		verify(builder).equal(join, "Tomato");
		verify(builder).or(predicate);
	}

	@Test
	@Disabled
	public void shouldGenerateSpecsForExcludeIngredientFilter() {
		Specification<Recipe> spec = recipeSpecGenerator.generateSpecs(null, null, null, "Peanut", null);
		when(root.join("ingredients")).thenReturn(join);
		when(builder.notEqual(join, "Peanut")).thenReturn(predicate);
		when(builder.or(predicate)).thenReturn(predicate);

		Predicate result = spec.toPredicate(root, query, builder);
		assertNotNull(result);
		verify(root).join("ingredients");
		verify(builder).notEqual(join, "Peanut");
		verify(builder).or(predicate);
	}

	@Test
	public void shouldGenerateSpecsForInstructionFilter() {
		Specification<Recipe> spec = recipeSpecGenerator.generateSpecs(null, null, null, null, "Bake");
		when(builder.like(root.get("instructions"), "%Bake%")).thenReturn(predicate);

		Predicate result = spec.toPredicate(root, query, builder);
		assertNotNull(result);
		verify(builder).like(root.get("instructions"), "%Bake%");
	}

	// Can be added more cases for this unit
	@Test
	public void shouldGenerateSpecsForMultipleFilters() {
		Specification<Recipe> spec = recipeSpecGenerator.generateSpecs(true, 4, "Tomato", null, "Bake");

		when(builder.equal(root.get("isVegetarian"), true)).thenReturn(predicate);
		when(builder.equal(root.get("servings"), 4)).thenReturn(predicate);
		when(root.join("ingredients")).thenReturn(join);
		when(builder.equal(join, "Tomato")).thenReturn(predicate);
		when(builder.like(root.get("instructions"), "%Bake%")).thenReturn(predicate);

		Predicate result = spec.toPredicate(root, query, builder);
		assertNotNull(result);

		verify(builder).equal(root.get("isVegetarian"), true);
		verify(builder).equal(root.get("servings"), 4);
		verify(root, times(1)).join("ingredients");

		verify(builder).equal(join, "Tomato");
		verify(builder).like(root.get("instructions"), "%Bake%");
	}
}
