package com.masa.karma_house.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class UserDto {
	private long id;
	private String name;
	private String login;
	private String email;
	private String password;

}