package de.egym.recruiting.codingtask.jpa.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;


@Entity
public class User extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(required = true, value = "Non-empty name of user.", example = "John")
	private String firstName;

	@ApiModelProperty(required = true, value = "Non-empty last name of user.", example = "Connor")
	private String lastName;

	@ApiModelProperty(required = true, value = "Unique email address with pattern like xxx@yyy.zz", example = "test+allowed_Also@fake.com")
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

	@JsonProperty
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	@JsonIgnore
	public void setBirthday(long birthday) {
		this.birthday = new Date(birthday);
	}
}
