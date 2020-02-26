package fr.lpr.membership.security;

import fr.lpr.membership.domain.User;
import fr.lpr.membership.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
@Slf4j
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	@Inject
	private UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(final String login) {
		log.debug("Authenticating {}", login);
		final String lowercaseLogin = login.toLowerCase();
		final Optional<User> userFromDatabase = userRepository.findOneByLogin(lowercaseLogin);
		return userFromDatabase.map(
				user -> {
					if (!user.isActivated()) {
						throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
					}
					final List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
							.map(authority -> new SimpleGrantedAuthority(authority.getName())).collect(Collectors.toList());
					return new org.springframework.security.core.userdetails.User(lowercaseLogin, user.getPassword(), grantedAuthorities);
				}).orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database"));
	}
}
