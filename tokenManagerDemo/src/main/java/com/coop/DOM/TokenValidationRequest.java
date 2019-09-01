package com.coop.DOM;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.jose.jwk.RSAKey;

public class TokenValidationRequest {
	
	private String idtoken;

	@JsonProperty
	private RSAKey rSAKey;
	

	public String getIdtoken() {
		return idtoken;
	}

	public void setIdtoken(String idtoken) {
		this.idtoken = idtoken;
	}
	
	public RSAKey getrSAKey() {
		return rSAKey;
	}

	public void setrSAKey(RSAKey rSAKey) {
		this.rSAKey = rSAKey;
	}

	
	
	
	

}
