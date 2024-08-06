package com.ag.myfavoriterecipes.service;

import com.ag.myfavoriterecipes.model.Recipe;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class RecipeSpecGenerator {

	public Specification<Recipe> generateSpecs(Boolean isVegetarian, Integer servings, String includeIngredient,
											   String excludeIngredient, String instruction) {
		return Specification
				.where(isVegetarian == null ? null : vegetarian(isVegetarian))
				.and(servings == null ? null : servingsTo(servings))
				.and(StringUtils.isEmpty(includeIngredient) ? null : includedIngredients(List.of(includeIngredient)))
				.and(StringUtils.isEmpty(excludeIngredient) ? null : excludedIngredients(List.of(excludeIngredient)))
				.and(StringUtils.isEmpty(instruction) ? null : instructionsLike(instruction));
	}

	private Specification<Recipe> vegetarian(final boolean vegetarian) {
		return (root, query, builder) -> builder.equal(root.get("isVegetarian"), vegetarian);
	}

	private Specification<Recipe> servingsTo(final int serving) {
		return (root, query, builder) -> builder.equal(root.get("servings"), serving);
	}

	private Specification<Recipe> includedIngredients(final List<String> ingredients) {
		return (root, query, builder) -> {
			Join<Recipe, String> ingredientsJoin = root.join("ingredients");
			Predicate[] predicates = ingredients.stream()
					.map(ingredient -> builder.equal(ingredientsJoin, ingredient))
					.toArray(Predicate[]::new);
			return builder.or(predicates);
		};
	}

	private Specification<Recipe> excludedIngredients(final List<String> ingredients) {
		return (root, query, builder) -> {
			Subquery<Long> subquery = query.subquery(Long.class);
			Root<Recipe> subRoot = subquery.from(Recipe.class);
			Join<Recipe, String> subIngredients = subRoot.join("ingredients");

			subquery.select(subRoot.get("id")).where(subIngredients.in(ingredients));

			return builder.not(root.get("id").in(subquery));
		};
	}

	private Specification<Recipe> instructionsLike(final String instruction) {
		return (root, query, builder) -> builder.like(root.get("instructions"), "%" + instruction + "%");

	}
}
