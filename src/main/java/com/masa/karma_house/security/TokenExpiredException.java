package com.masa.karma_house.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class TokenExpiredException extends RuntimeException {
	
	public TokenExpiredException(String reason) {
		super(reason);
	}

}
