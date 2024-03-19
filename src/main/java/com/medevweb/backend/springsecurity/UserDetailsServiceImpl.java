package com.medevweb.backend.springsecurity;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.medevweb.backend.models.dao.IUserDao;
import com.medevweb.backend.models.entity.Role;



@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private IUserDao usuariodao;

	@Override
	public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
		
		com.medevweb.backend.models.entity.User user = usuariodao.findByUsername(correo);
		if (user == null) {
			throw new UsernameNotFoundException(correo);
		}
		List<String> rolesAsString = user.getRoles().stream().map(Role::getName) // Obtiene el nombre del rol como cadena
				.collect(Collectors.toList());		
		String contrasenia = user.getPassword();
		if (contrasenia == null) {
			contrasenia = "signin_with_external_account";
		} else {
			contrasenia = user.getPassword();
		}
		return User.withUsername(correo).password(contrasenia).roles(rolesAsString.toArray(new String[0])).build();
	}

	public static class Usuario {
		private String username;
		private String password;
		private Set<String> roles;

		public Usuario(String username, String password, Set<String> roles) {
			this.username = username;
			this.password = password;
			this.roles = roles;
		}

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}

		public Set<String> getRoles() {
			return roles;
		}
	}
}
