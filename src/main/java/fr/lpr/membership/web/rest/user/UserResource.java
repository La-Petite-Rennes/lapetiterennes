package fr.lpr.membership.web.rest.user;

import fr.lpr.membership.domain.User;
import fr.lpr.membership.repository.UserRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserResource {

	private final UserRepository userRepository;

	/**
	 * GET /users -&gt; get all users.
	 *
	 * @return all users
	 */
	@GetMapping
	@Timed
	public List<User> getAll() {
		log.debug("REST request to get all Users");
		return userRepository.findAll();
	}

	/**
	 * GET /users/:login -&gt; get the "login" user.
	 *
	 * @param login
	 *            the login
	 * @return the user
	 */
	@GetMapping("/{login}")
	@Timed
	public ResponseEntity<User> getUser(@PathVariable String login) {
		log.debug("REST request to get User : {}", login);
		return userRepository.findOneByLogin(login)
            .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
}
