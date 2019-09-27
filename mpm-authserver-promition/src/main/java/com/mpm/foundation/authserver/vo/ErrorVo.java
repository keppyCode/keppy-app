package com.mpm.foundation.authserver.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorVo {
	@JsonProperty("error") 
	private String error;
	@JsonProperty("error_description") 
	private String description;

	public ErrorVo(String error, String description) {
		super();
		this.error = error;
		this.description = description;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
