package com.medevweb.backend.springsecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 este filtro se encarga de interceptar las solicitudes entrantes,
 extraer el JWT del encabezado de autorización, validar el token y
 autenticar al usuario correspondiente si el token es válido. Luego,
 establece la autenticación en el contexto de seguridad de Spring,
 lo que permitirá que el usuario acceda a los recursos protegidos por la aplicación.
 * */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtUtilService jwtUtilService;

	@Override
	protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain chain)
			throws jakarta.servlet.ServletException, IOException {

		try {
			final String authorizationHeader = request.getHeader("Authorization");
			String username = null;
			String jwt = null;

			if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
				jwt = authorizationHeader.substring(7);
				username = jwtUtilService.extractUsername(jwt);
			}

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
				if (jwtUtilService.validateToken(jwt, userDetails)) {
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				}
			}
			chain.doFilter(request, response);
		} catch (Exception e) {
			e.printStackTrace();			
			response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);		
			response.getWriter().write("The jwt has expired or is invalid");
		}
	}
}
