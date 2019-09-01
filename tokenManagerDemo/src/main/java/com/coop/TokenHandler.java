package com.coop;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coop.DOM.IdToken;
import com.coop.DOM.IdTokenHeader;
import com.coop.DOM.IdTokenPayload;
import com.coop.DOM.IdTokenResponse;
import com.coop.DOM.IdtokenRequest;
import com.coop.DOM.TokenValidationRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import net.minidev.json.JSONObject;


@RestController
public class TokenHandler {
	
	static RSAKey rsaPublicJWK ;
	static RSAKey rsaJWK;
	static File file;
	
	TokenHandler(){
		file= new File("my-key-store.json");
	/*	
		try {
			rsaJWK = new RSAKeyGenerator(2048)
				    .keyID("123")
				    .generate();
		} catch (JOSEException e) {
			
			e.printStackTrace();
		}
		 rsaPublicJWK = rsaJWK.toPublicJWK();*/
	}

	
	
	@GetMapping (value="/authngw/entry-point")
	public void checkentryPoint(@RequestParam("login_challenge") String login_challenge,@RequestParam ("brand") String brand ) {
		
		
		System.out.println(login_challenge +"    "+ brand);
	}
	
	
	
	
	@PostMapping(value="parse")
	public IdToken parse(@RequestBody IdtokenRequest idTokenRequest) {
			JWSObject jwsObject    = null;
			IdTokenPayload payload =null;
			IdTokenHeader header   =null;
			ObjectMapper mapper = new ObjectMapper();
			IdToken idtoken = new IdToken();
	try {
			jwsObject = JWSObject.parse(idTokenRequest.getIdtoken());
			 header   = mapper.readValue(jwsObject.getHeader().toJSONObject().toJSONString(), IdTokenHeader.class);
			 payload  =	mapper.readValue(jwsObject.getPayload().toJSONObject().toJSONString(), IdTokenPayload.class);
			 idtoken.setHeader(header);
		     idtoken.setPayload(payload);
			 idtoken.setSignature(jwsObject.getSignature().toJSONString());
			 
			 
		        
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         

		return idtoken;
	}
	

	@GetMapping(value="getKeys")
	public String getStaticKey() 
	{
	 	
	String keySet=	getStringFromFile("keyStore/my-key-store.json");	
		return keySet;	
		
	}
	
	
	//generate token in the form hydra generate token 
	//this token is signed token
	@GetMapping(value="getToken")
	public IdTokenResponse generateToken() {
		
		String token=null;
		IdTokenResponse idTokenResponse=new IdTokenResponse();
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		try {
			/*rsaJWK = new RSAKeyGenerator(2048)
				    .keyID("123")
				    .generate();
		
			 rsaPublicJWK = rsaJWK.toPublicJWK();*/
			JWSSigner signer = new RSASSASigner(rsaJWK);
			
		
			String[] str= {"Personal Online Banking","Open Online Banking"};
			
			List audence=Arrays.asList(str); 
			JWTClaimsSet claimsSet = null;
			try {
				claimsSet = new JWTClaimsSet.Builder()
					    .subject("CB1234567100")
					    .issuer("https://localhost:4444/")
					    .expirationTime(new Date(new Date().getTime() + 60 * 1000))
					    .audience(audence)
					    .jwtID("bd4f0bd5-324f-4a26-8d7d-2993e7edefb1")
					    .issueTime(new Date())
					    .expirationTime(simpleDateFormat.parse("2020-09-09"))
					    .build();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

				SignedJWT signedJWT = new SignedJWT(
				    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
				    claimsSet);
				signedJWT.sign(signer);
				
			 token = signedJWT.serialize();
		} catch (JOSEException e) {
			
			e.printStackTrace();
		}
		
		idTokenResponse.setIdtoken(token);
		
		return idTokenResponse;
	}
	
	
	
	
	//read token form request and verify using store application key
	@PostMapping(value="verifyToken")
	public String verifyToken(@RequestBody IdtokenRequest idTokenRequest) {
		
		
		JWSObject signedJWT    = null;
		
		boolean result = false;
		String token=null;

		try {
			signedJWT = JWSObject.parse(idTokenRequest.getIdtoken());
				
		    try {
		    	//here we are verifying the token usinng public key
		    	//if token verifyed successfully it return true otherwise false
					JWSVerifier verifier = new RSASSAVerifier(rsaPublicJWK);
					result=signedJWT.verify(verifier);
			    } catch (JOSEException e) {
					
					e.printStackTrace();
			   }
						
		     } catch (ParseException e) {
			
			e.printStackTrace();
		}
			if(result==true) {
				return "token validated Successfully";	
			}
			
			else {
				return "not token validated ";	
			}
		
	}
	
	//generate new key for every new request
		@GetMapping(value="getkey")
	public String getKey() {
		// Generate the RSA key pair
		KeyPairGenerator gen=null;
		try {	
			gen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gen.initialize(2048);
		KeyPair keyPair = gen.generateKeyPair();

		// Convert to JWK format
	 rsaJWK = new RSAKey.Builder((RSAPublicKey)keyPair.getPublic())
		    .privateKey((RSAPrivateKey)keyPair.getPrivate())
		    .keyUse(KeyUse.SIGNATURE).keyID("123")
		   // .keyID(UUID.randomUUID().toString())
		    .build();
		rsaPublicJWK=(RSAKey) rsaJWK.toPublicJWK();
		return rsaPublicJWK.toJSONString();
		
		
	}
	
/*	@GetMapping(value="getRSAkey")
	public RSAKey getRSAKey() {

		try {
			rsaJWK = new RSAKeyGenerator(2048)
				    .keyID("123")
				    .generate();
		} catch (JOSEException e) {
			
			e.printStackTrace();
		}
		 rsaPublicJWK = rsaJWK.toPublicJWK();
		
		return rsaPublicJWK;
		
		
	}*/
	
@RequestMapping(value="validateIdToken", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE,headers="Accept=application/json")
public String ValidateIdToken(@RequestBody TokenValidationRequest tokenValidationRequest) {
	
	
	JWSObject signedJWT    = null;
	
	boolean result = false;
	String token=null;

	try {
		signedJWT = JWSObject.parse(tokenValidationRequest.getIdtoken());
			
	    try {
	    	//here we are verifying the token usinng public key
	    	//if token verifyed successfully it return true otherwise false
	    	JWSVerifier verifier = new RSASSAVerifier(rsaPublicJWK);
				//JWSVerifier verifier = new RSASSAVerifier(tokenValidationRequest.getrSAKey());
				result=signedJWT.verify(verifier);
		    } catch (JOSEException e) {
				
				e.printStackTrace();
		   }
					
	     } catch (ParseException e) {
		
		e.printStackTrace();
	}
		if(result==true) {
			return "token validated Successfully";	
		}
		
		else {
			return "not token validated ";	
		}
	
}

//read key form file and set as rsakey for application
@PostMapping (value="setRsaKey")
public JWKSet setKey() {
	
	
	JWKSet localKeys = null;
	try {
		/*BufferedWriter writer=	getBufferedWriter("keyStore/my-key-store.json");
		writer.flush();
		writer.write(jsonObject.toJSONString());*/
		localKeys = JWKSet.load(file);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	JWK jwk=localKeys.getKeyByKeyId("123");
	rsaPublicJWK=(RSAKey) jwk;
	return localKeys;
	
}


//get key as string and store into file
@PostMapping (value="storeKey")
public String setKeys(@RequestBody JSONObject jsonObject) {
	
	       
		try {
		
		FileWriter fw=new FileWriter(file,false);
		fw.write(jsonObject.toJSONString());
		fw.flush();
		fw.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return "key set successfully";

	
	
}



private String getStringFromFile(String stubPath) {
	String filedata = null;
	try {
		ClassPathResource resource = new ClassPathResource(stubPath);

		BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
		filedata = reader.lines().collect(Collectors.joining("\n"));
		reader.close();
	} catch (IOException ex) {
		throw new RuntimeException(ex);
	}
	return filedata;
}


private InputStream getFileInputStream(String stubPath) {
	String filedata = null;
InputStream is =null;
		ClassPathResource resource = new ClassPathResource(stubPath);

	try {
	is=	resource.getInputStream();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return is;
	
}
private BufferedWriter getBufferedWriter(String stubPath) {
	String filedata = null;
File file =null;
BufferedWriter writer=null;
		ClassPathResource resource = new ClassPathResource(stubPath);

	try {
	file=	resource.getFile();
	 writer = new BufferedWriter(new FileWriter(file,false));
	
	
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return writer;
	
}
	
	}


	
	

