package com.ag.myfavoriterecipes.controller.dto;

import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecipeDto {
	private Long id;
	private String name;
	private boolean vegetarian;
	private int servings;
	private String instructions;
	private Set<String> ingredients;
}
