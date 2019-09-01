package com.coop.DOM;

public class IdToken {
	
	private IdTokenHeader header;
	private IdTokenPayload payload;
	private String signature;
	
	public IdToken() {
		
	}

	public IdToken(IdTokenHeader header, IdTokenPayload payload, String signature) {
		super();
		this.header = header;
		this.payload = payload;
		this.signature = signature;
	}

	public IdTokenHeader getHeader() {
		return header;
	}

	public void setHeader(IdTokenHeader header) {
		this.header = header;
	}

	public IdTokenPayload getPayload() {
		return payload;
	}

	public void setPayload(IdTokenPayload payload) {
		this.payload = payload;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
	

}
