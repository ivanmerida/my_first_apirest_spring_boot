package com.medevweb.backend.auth;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class AuthorizationServerConfig {
	private static final int EXPIRATION_IN_MINUTES = 1;
	private static final String SECRET_KEY = "MEDEVWEB MY FIRST API-REST WITH SPRING BOOT 3 !! HELLO WORLD";

	
	private static void load() {
		Map<String, Object> extraClaims = buildExtraClaims();
		String jwt = buildJws(extraClaims);
		
		System.out.println("TOKEN GENERADO: "+jwt);
		try {
//			Thread.sleep(60 * 1000 );
			Claims payload =  verifyJws(jwt+"a");
			System.out.println("TOKEN DECODIFICADO: "+payload.getSubject());
		}catch(JwtException   e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	
	
	private static Claims verifyJws(String jwt) {
		return Jwts.parser()
			.verifyWith(generateKey())
			.build()
			.parseSignedClaims(jwt)
			.getPayload();
		

	}

	private static String buildJws(Map<String, Object> extraClaims) {
		Date issuedAt = new Date(System.currentTimeMillis());
		Date expiration = new Date(issuedAt.getTime() + (EXPIRATION_IN_MINUTES * 60* 1000));

		String jwt = Jwts.builder()
				.header()
				.type("JWT")
				.and()
				.subject("usuario.db")
				.expiration(expiration)
				.issuedAt(issuedAt)
				.claims(extraClaims)
				.signWith(generateKey(), Jwts.SIG.HS256)
				.compact();
		return jwt;
	}

	private static Map<String, Object> buildExtraClaims() {
		Map<String, Object> extraClaims = new HashMap<>();
		extraClaims.put("name",  "usuario mar");
		return extraClaims;
	}

	private static SecretKey generateKey() {
		// TODO Auto-generated method stub
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes()); // Para generar el secrey key

	}
	
	
}
