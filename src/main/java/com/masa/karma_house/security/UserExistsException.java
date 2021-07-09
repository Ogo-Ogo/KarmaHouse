package com.masa.karma_house.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class UserExistsException extends AuthenticationException {

	private static final long serialVersionUID = -2654213378325613475L;

	public UserExistsException(String message) {
		super(message);
	}

}
