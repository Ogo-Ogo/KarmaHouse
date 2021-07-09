package com.masa.karma_house.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class UserRegisterDto {
	private String name;
	private String login;
	private String email;
	private String password;

}
