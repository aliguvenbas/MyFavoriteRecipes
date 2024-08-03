package com.ag.myfavoriterecipes.repository;

import com.ag.myfavoriterecipes.model.Recipe;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
	List<Recipe> findByIsVegetarian(boolean isVegetarian);
	List<Recipe> findByServings(int servings);
	List<Recipe> findByIngredientsContaining(String ingredient);
	List<Recipe> findByInstructionsContaining(String instruction);
}

