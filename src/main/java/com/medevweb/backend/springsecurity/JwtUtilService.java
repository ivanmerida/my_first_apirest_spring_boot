package com.medevweb.backend.springsecurity;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.medevweb.backend.models.entity.User;

@SuppressWarnings("deprecation")
@Service
public class JwtUtilService {
	@Autowired
	private com.medevweb.backend.models.dao.IUserDao userdao;
	@Autowired
	private UserDetailsService userDetailsService;
	// ModeloDual => [Base64] => TW9kZWxvRHVhbA==
	private static final String JWT_SECRET_KEY = "TW9kZWxvRHVhbA==";
//	public static final long JWT_TOKEN_VALIDITY = 1000 * 10 * 1; //  10 seg
//	public static final long JWT_REFRESH_TOKEN_VALIDITY = 1000 * 10 * 1; //10 seg
	public static final long JWT_TOKEN_VALIDITY = 1000 * 60 * 60; //  5 minutos
	public static final long JWT_REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24; // 24 horas
//  public static final long JWT_TOKEN_VALIDITY = 1000 * 60 * 60 * (long) 8; //8 horas

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		return claimsResolver.apply(extractAllClaims(token));
	}

	private Claims extractAllClaims(String token) {
		return Jwts
	            .parser()
	            .verifyWith(generateKey())
	            .build()
	            .parseSignedClaims(token)
	            .getPayload();
	}
	
	private static SecretKey generateKey(){
		return Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes()); // Para generar el secrey key

	}
	

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();

		// Agregar información adicional como "claim"
		List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		if (roles != null) {
			User user = userdao.findByUsername(userDetails.getUsername());
			StringBuilder jsonBuilder = new StringBuilder();
			jsonBuilder.append("{");
			jsonBuilder.append("\"id\":\"").append(user.getId()).append("\",");
//			jsonBuilder.append("\"name\":\"").append(user.getName()).append("\",");
//			jsonBuilder.append("\"surname\":\"").append(user.getSurname()).append("\",");
			jsonBuilder.append("\"username\":\"").append(user.getUsername()).append("\",");
//			jsonBuilder.append("\"email\":\"").append(user.getEmail()).append("\"");
//			jsonBuilder.append("\"numTelefono\":\"").append(client.getNumTelefono()).append("\",");
//			jsonBuilder.append("\"verificado\":").append(mUsuario.getVerificado());
			jsonBuilder.append("}");
			String json = jsonBuilder.toString();
			claims.put("rols", roles);
			claims.put("refresh_token", generateRefreshToken(userDetails));
			claims.put("user", json);
		}

		return createToken(claims, userDetails.getUsername(), JWT_TOKEN_VALIDITY);
	}

	public String generateRefreshToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();

		// Agregar información adicional como "claim"
		GrantedAuthority rol = userDetails.getAuthorities().stream().findFirst().orElse(null);

		if (rol != null) {
			claims.put("rol", rol.getAuthority());
		}

		return createToken(claims, userDetails.getUsername(), JWT_REFRESH_TOKEN_VALIDITY);
	}
	
	private String createToken(Map<String, Object> claims, String subject, Long jwtTokenValidity) {

		 return Jwts.builder()
					.header()
					.type("JWT")
					.and()
					.subject(subject)
					.expiration(new Date(System.currentTimeMillis() + jwtTokenValidity))
					.issuedAt(new Date(System.currentTimeMillis()))
					.claims(claims)
					.signWith(generateKey(), Jwts.SIG.HS256)
					.compact();
		 
		 
		 /* //ESTA ES LA versión deprecada pero sirve de referencia
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + jwtTokenValidity))
				.signWith(SignatureAlgorithm.HS256, JWT_SECRET_KEY).compact();*/
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	public UserDetails getUserDetailsFromToken(String token) {
		final String username = extractUsername(token);

		// Utilizar el UserDetailsService para cargar los detalles completos del usuario
		UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

		return userDetails;
	}

	public String extractRefreshToken(String jwt) {
		Claims claims = extractAllClaims(jwt);
		return (String) claims.get("refresh_token");
	}

	public String validateRefreshToken(String paramJwt) {
		String jwt = paramJwt;
		String username = extractUsername(jwt);
		if (username != null) {
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
			if (validateToken(jwt, userDetails)) {
				String newJwt = generateToken(userDetails);
				return newJwt;
			}
		}
		return "The jwt has expired or is invalid";
	}

}
