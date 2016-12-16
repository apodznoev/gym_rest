package de.egym.recruiting.codingtask.jpa.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;


@Entity
public class User extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	private String firstName;

	private String lastName;

	@Column(unique = true, nullable = false)
	private String email;

	@ApiModelProperty(required = true, value = "Date in ISO format, i.e., yyyy-MM-dd.", example = "1987-06-15")
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date birthday;

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
}
