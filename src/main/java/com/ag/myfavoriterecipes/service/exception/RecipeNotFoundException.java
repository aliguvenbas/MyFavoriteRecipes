package com.ag.myfavoriterecipes.service.exception;

public class RecipeNotFoundException extends RuntimeException {
	public RecipeNotFoundException(Long id) {
		super("Recipe not found with id: " + id);
	}
}
