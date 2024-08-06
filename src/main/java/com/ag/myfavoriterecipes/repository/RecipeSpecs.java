package com.ag.myfavoriterecipes.repository;

import com.ag.myfavoriterecipes.model.Recipe;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class RecipeSpecs {
	public static Specification<Recipe> vegetarian(final boolean vegetarian) {
		return (root, query, builder) -> builder.equal(root.get("isVegetarian"), vegetarian);
	}

	public static Specification<Recipe> servingsTo(final int serving) {
		return (root, query, builder) -> builder.equal(root.get("servings"), serving);
	}

	public static Specification<Recipe> includedIngredients(final List<String> ingredients) {
		return (root, query, builder) -> {
			Join<Recipe, String> ingredientsJoin = root.join("ingredients");
			Predicate[] predicates = ingredients.stream()
					.map(ingredient -> builder.equal(ingredientsJoin, ingredient))
					.toArray(Predicate[]::new);
			return builder.or(predicates);
		};
	}

	public static Specification<Recipe> excludedIngredients(final List<String> ingredients) {
		return (root, query, builder) -> {
			Join<Recipe, String> ingredientsJoin = root.join("ingredients");
			Predicate[] predicates = ingredients.stream()
					.map(ingredient -> builder.notEqual(ingredientsJoin, ingredient))
					.toArray(Predicate[]::new);
			return builder.or(predicates).not();
		};
	}

//	public static Specification<Recipe> includedIngredients(final List<String> includeIngredientIngredients) {
//
////		return (root, query, builder) -> {
////			Join<Doctor,Hospital> hospitalDoctors = root.join("doctors");
////			return builder.equal(hospitalDoctors.get("speciality"), speciality);
////		};
//				return (root, query, builder) -> {
//			Join<String,Recipe> hospitalDoctors = root.join("ingredients");
//			return builder.equal(hospitalDoctors.get("speciality"), speciality);
//		};
//
//		return (root, query, builder) -> root.get("ingredients").in(includeIngredientIngredients);
//	}
//
//	public static Specification<Recipe> excludedIngredients(final List<String> excludeIngredients) {
//		return (root, query, builder) -> root.get("ingredients").in(excludeIngredients).not();
//	}

	public static Specification<Recipe> instructionsLike(final String instruction) {
		return (root, query, builder) -> builder.like(root.get("instructions"), "%" + instruction + "%");

	}
}
