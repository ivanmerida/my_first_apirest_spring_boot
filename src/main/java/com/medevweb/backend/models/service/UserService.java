package com.medevweb.backend.models.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medevweb.backend.models.dao.IUserDao;
import com.medevweb.backend.models.entity.User;




@Service
public class UserService implements UserDetailsService{
	private Logger logger = LoggerFactory.getLogger(UserService.class);
	@Autowired // PARA INYECTARLO
	private IUserDao userDao;

	@Override
	@Transactional(readOnly = true) // DEBE SER DE SPRINGBOOT NO DE JAKARTA
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userDao.findByUsername(username);
		if(user == null) {
			logger.error("User doesn't exist in the system: "+user);
			throw new UsernameNotFoundException("User doesn't exist in the system: "+user);
		}
		// Aqui lo convierte en un GrantedAuthority
		List<GrantedAuthority> authorities = user.getRoles()
				.stream()
				.map(role -> new SimpleGrantedAuthority(role.getName()))
				.peek(authority -> logger.info("Role: "+authority.getAuthority()))
				.collect(Collectors.toList());

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), 
				user.getEnabled(), true, true, true, null);
	} 

}























