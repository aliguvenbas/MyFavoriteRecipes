package com.ag.myfavoriterecipes.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FilterDto {
	@Schema(nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private Boolean vegetarian;
	@Schema(nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private Integer servings;
	@Schema(nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String includeIngredient;
	@Schema(nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String excludeIngredient;
	@Schema(nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String instructions;
}
