package com.ag.myfavoriterecipes.service.exception;

public class NoValidFilterException extends RuntimeException{

	public NoValidFilterException() {
		super("Search function can not be use without any filter.");
	}
}
