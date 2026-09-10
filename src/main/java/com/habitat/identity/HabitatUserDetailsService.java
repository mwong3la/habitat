package com.habitat.identity;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HabitatUserDetailsService implements UserDetailsService {

	private final AppUserRepository userRepository;

	public HabitatUserDetailsService(AppUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AppUser user = userRepository.findByEmailIgnoreCase(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return User.withUsername(user.getEmail())
				.password(user.getPasswordHash())
				.disabled(!user.isEnabled())
				.authorities(user.getRoles().stream().map(role -> role.getName().name()).toArray(String[]::new))
				.build();
	}
}
