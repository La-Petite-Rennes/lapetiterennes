package fr.lpr.membership.web.rest.user;

import com.google.common.base.Strings;
import fr.lpr.membership.domain.Authority;
import fr.lpr.membership.domain.PersistentToken;
import fr.lpr.membership.domain.User;
import fr.lpr.membership.repository.PersistentTokenRepository;
import fr.lpr.membership.repository.UserRepository;
import fr.lpr.membership.security.AuthoritiesConstants;
import fr.lpr.membership.security.SecurityUtils;
import fr.lpr.membership.service.MailService;
import fr.lpr.membership.service.UserService;
import fr.lpr.membership.web.rest.dto.UserDTO;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/account")
@Slf4j
@RequiredArgsConstructor
public class AccountResource {

	private final UserRepository userRepository;

	private final UserService userService;

	private final PersistentTokenRepository persistentTokenRepository;

	private final MailService mailService;

	/**
	 * POST /register -&gt; register the user.
	 *
	 * @param userDTO
	 *            the user
	 * @param request
	 *            the http request
	 * @return result of the creation
	 */
	@PostMapping(value = "/account/register", produces = MediaType.TEXT_PLAIN_VALUE)
	@Timed
	@RolesAllowed(AuthoritiesConstants.ADMIN)
	public ResponseEntity<?> registerAccount(@Validated @RequestBody UserDTO userDTO, HttpServletRequest request) {
		return userRepository
				.findOneByLogin(userDTO.getLogin())
				.map(user -> new ResponseEntity<>("login already in use", HttpStatus.BAD_REQUEST))
				.orElseGet(
						() -> userRepository
						.findOneByEmail(userDTO.getEmail())
						.map(user -> new ResponseEntity<>("e-mail address already in use", HttpStatus.BAD_REQUEST))
						.orElseGet(
								() -> {
									final User user = userService.createUserInformation(userDTO.getLogin(), userDTO.getPassword(),
											userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail().toLowerCase(), userDTO.getLangKey());
									final String baseUrl = request.getScheme() + // "http"
											"://" + // "://"
											request.getServerName() + // "myhost"
											":" + // ":"
											request.getServerPort(); // "80"

									mailService.sendActivationEmail(user, baseUrl);
									return new ResponseEntity<>(HttpStatus.CREATED);
								}));
	}

	/**
	 * GET /activate -&gt; activate the registered user.
	 *
	 * @param key
	 *            the key
	 * @return result of the account activation
	 */
	@GetMapping("/account/activate")
	@Timed
	public ResponseEntity<String> activateAccount(@RequestParam(value = "key") String key) {
		return Optional.ofNullable(userService.activateRegistration(key)).map(user -> new ResponseEntity<String>(HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * GET /authenticate -&gt; check if the user is authenticated, and return its login.
	 *
	 * @param request
	 *            the http request
	 * @return login of the user
	 */
	@GetMapping("/account/authenticate")
	@Timed
	public String isAuthenticated(HttpServletRequest request) {
		log.debug("REST request to check if the current user is authenticated");
		return request.getRemoteUser();
	}

	/**
	 * GET /account -&gt; get the current user.
	 *
	 * @return the current user
	 */
	@GetMapping
	@Timed
	public ResponseEntity<UserDTO> getAccount() {
		return Optional
				.ofNullable(userService.getUserWithAuthorities())
				.map(user -> new ResponseEntity<>(new UserDTO(user.getLogin(), null, user.getFirstName(), user.getLastName(), user.getEmail(), user
						.getLangKey(), user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toCollection(LinkedList::new))), HttpStatus.OK))
						.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * POST /account -&gt; update the current user information.
	 *
	 * @param userDTO
	 *            the user
	 * @return result of the update
	 */
	@PostMapping
	@Timed
	public ResponseEntity<String> saveAccount(@RequestBody UserDTO userDTO) {
		return userRepository.findOneByLogin(userDTO.getLogin()).filter(u -> u.getLogin().equals(SecurityUtils.getCurrentLogin())).map(u -> {
			userService.updateUserInformation(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), userDTO.getLangKey());
			return new ResponseEntity<String>(HttpStatus.OK);
		}).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * POST /change_password -&gt; changes the current user's password
	 *
	 * @param password
	 *            the new password
	 * @return result of the update
	 */
	@PostMapping("/change_password")
	@Timed
	public ResponseEntity<?> changePassword(@RequestBody String password) {
		if (Strings.isNullOrEmpty(password) || password.length() < 5 || password.length() > 50) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		userService.changePassword(password);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * GET /account/sessions -&gt; get the current open sessions.
	 *
	 * @return the current open sessions
	 */
	@GetMapping("/sessions")
	@Timed
	public ResponseEntity<List<PersistentToken>> getCurrentSessions() {
		return userRepository.findOneByLogin(SecurityUtils.getCurrentLogin())
				.map(user -> new ResponseEntity<>(persistentTokenRepository.findByUser(user), HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * DELETE /account/sessions?series={series} -&gt; invalidate an existing session.
	 *
	 * - You can only delete your own sessions, not any other user's session - If you delete one of your existing sessions, and that you are currently logged in
	 * on that session, you will still be able to use that session, until you quit your browser: it does not work in real time (there is no API for that), it
	 * only removes the "remember me" cookie - This is also true if you invalidate your current session: you will still be able to use it until you close your
	 * browser or that the session times out. But automatic login (the "remember me" cookie) will not work anymore. There is an API to invalidate the current
	 * session, but there is no API to check which session uses which cookie.
	 *
	 * @param series
	 *            the series
     */
	@DeleteMapping("/sessions/{series}")
	@Timed
	public void invalidateSession(@PathVariable String series) {
		final String decodedSeries = URLDecoder.decode(series, StandardCharsets.UTF_8);
		userRepository.findOneByLogin(SecurityUtils.getCurrentLogin())
            .flatMap(u -> persistentTokenRepository.findByUser(u).stream().filter(persistentToken -> Objects.equals(persistentToken.getSeries(), decodedSeries))
            .findAny())
            .ifPresent(t -> persistentTokenRepository.deleteById(decodedSeries));
	}

	@PostMapping(value = "/reset_password/init", produces = MediaType.TEXT_PLAIN_VALUE)
	@Timed
	public ResponseEntity<?> requestPasswordReset(@RequestBody String mail, HttpServletRequest request) {

		return userService.requestPasswordReset(mail).map(user -> {
			final String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
			mailService.sendPasswordResetMail(user, baseUrl);
			return new ResponseEntity<>("e-mail was sent", HttpStatus.OK);
		}).orElse(new ResponseEntity<>("e-mail address not registered", HttpStatus.BAD_REQUEST));

	}

	@PostMapping("/reset_password/finish")
	@Timed
	public ResponseEntity<String> finishPasswordReset(@RequestParam(value = "key") String key, @RequestParam(value = "newPassword") String newPassword) {
		return userService.completePasswordReset(newPassword, key).map(user -> new ResponseEntity<String>(HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}
}
