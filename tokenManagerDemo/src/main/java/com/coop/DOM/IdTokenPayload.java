package com.coop.DOM;

import java.util.ArrayList;
import java.util.List;

public class IdTokenPayload {
	
	private String acr;
	 private String at_hash;
	 String[] aud ;
	 private float auth_time;
	 private float exp;
	 private float iat;
	 private String iss;
	 private String jti;
	 private String nonce;
	 private float rat;
	 private String sub;
	 
	 
	public String getAcr() {
		return acr;
	}
	public void setAcr(String acr) {
		this.acr = acr;
	}
	public String getAt_hash() {
		return at_hash;
	}
	public void setAt_hash(String at_hash) {
		this.at_hash = at_hash;
	}


	public String[] getAud() {
		return aud;
	}
	public void setAud(String[] aud) {
		this.aud = aud;
	}
	public float getAuth_time() {
		return auth_time;
	}
	public void setAuth_time(float auth_time) {
		this.auth_time = auth_time;
	}
	public float getExp() {
		return exp;
	}
	public void setExp(float exp) {
		this.exp = exp;
	}
	public float getIat() {
		return iat;
	}
	public void setIat(float iat) {
		this.iat = iat;
	}
	public String getIss() {
		return iss;
	}
	public void setIss(String iss) {
		this.iss = iss;
	}
	public String getJti() {
		return jti;
	}
	public void setJti(String jti) {
		this.jti = jti;
	}
	public String getNonce() {
		return nonce;
	}
	public void setNonce(String nonce) {
		this.nonce = nonce;
	}
	public float getRat() {
		return rat;
	}
	public void setRat(float rat) {
		this.rat = rat;
	}
	public String getSub() {
		return sub;
	}
	public void setSub(String sub) {
		this.sub = sub;
	}
	 
	 
	 
	 

}
