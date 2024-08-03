package com.ag.myfavoriterecipes.controller.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecipeDto {
	private Long id;
	private String name;
	private boolean isVegetarian;
	private int servings;
	private String instructions;
	private Set<String> ingredients;
}
