package com.ag.myfavoriterecipes.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecipeDto {
	private Long id;
	@NotEmpty(message = "Name cannot be blank")
	private String name;
	private boolean vegetarian;
	private int servings;
	@NotEmpty(message = "instructions cannot be blank")
	private String instructions;
	private Set<String> ingredients;
}
