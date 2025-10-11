package org.project.shopservice.exceptions;

public class UserAlreadyExistException extends CloneNotSupportedException {
	public UserAlreadyExistException(String message) {
		super(message);
	}
}
