package com.ag.myfavoriterecipes.repository;

import com.ag.myfavoriterecipes.model.Recipe;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class RecipeSpecs {
	public static Specification<Recipe> vegetarian(boolean vegetarian) {
		return (root, query, builder) -> builder.equal(root.get("isVegetarian"), vegetarian);
	}

	public static Specification<Recipe> servingsTo(int serving) {
		return (root, query, builder) -> builder.equal(root.get("servings"), serving);
	}

	public static Specification<Recipe> includedIngredients(List<String> ingredients) {
		return (root, query, builder) -> root.get("ingredients").in(ingredients);
	}

//	public static Specification<Recipe> outOfIngredients(List<String> ingredients) {
//		return (root, query, builder) -> root.get("ingredients").(ingredients);
//	}

	public static Specification<Recipe> instructionsLike(String instructions) {
		return (root, query, builder) -> builder.like(root.get("instructions"), "%" + instructions + "%");
	}
}
