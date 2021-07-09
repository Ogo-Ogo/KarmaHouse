package com.masa.karma_house.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(of = { "id, name" })
@ToString
public class User {

	public static final int START_SEQ = 1;

	@Id
	@SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1, initialValue = START_SEQ)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
	@Setter(value=AccessLevel.NONE)
	private long id;

	@NotNull
	private String name;

	@NotNull
	private String login;

	@Column(name = "email", nullable = false, unique = true)
	@Email
	@NotBlank
	@Size(max = 100)
	private String email;

	@Column(name = "password", nullable = false)
	@NotBlank
	@Size(min = 5, max = 100)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;


	public User(String name, String login, String email, String password) {
		this.name = name;
		this.login = login;
		this.email = email;
		this.password = password;
	}
}