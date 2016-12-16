package de.egym.recruiting.codingtask.dto;

/**
 * This Data Transfer Object (DTO) is used to pass error codes and error messages via the REST interface from the server to the client.
 * This DTO is sent to the client using a JSON format.
 *
 * @see de.egym.recruiting.codingtask.rest.RestExceptionMapper
 */
public class ErrorDto {

	/**
	 * The error status code.
	 */
	private int errorCode;

	/**
	 * (Optional) Error text value.
	 */
	private String errorValue;

	/**
	 * Used for serialization only.
	 */
	ErrorDto() {
		super();
	}

	public ErrorDto(int errorCode, String errorValue) {
		this.errorCode = errorCode;
		this.errorValue = errorValue;
	}

	public int getErrorCode() {
		return errorCode;
	}

	void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorValue() {
		return errorValue;
	}

	void setErrorValue(String errorValue) {
		this.errorValue = errorValue;
	}
}
