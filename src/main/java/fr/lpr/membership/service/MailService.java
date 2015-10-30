package fr.lpr.membership.service;

import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.domain.User;

/**
 * <p>
 * Service for sending e-mails.
 * </p>
 * <p>
 * We use the @Async annotation to send e-mails asynchronously.
 * </p>
 */
@Service
public class MailService {

	private final Logger log = LoggerFactory.getLogger(MailService.class);

	@Inject
	private Environment env;

	@Inject
	private JavaMailSenderImpl javaMailSender;

	@Inject
	private MessageSource messageSource;

	@Inject
	private SpringTemplateEngine templateEngine;

	/**
	 * System default email address that sends the e-mails.
	 */
	private String from;

	@PostConstruct
	public void init() {
		this.from = env.getProperty("mail.from");
	}

	@Async
	public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) throws MessagingException {
		log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart, isHtml, to, subject, content);

		// Prepare message using a Spring helper
		final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
		message.setTo(to);
		message.setFrom(from);
		message.setSubject(subject);
		message.setText(content, isHtml);
		javaMailSender.send(mimeMessage);
		log.debug("Sent e-mail to User '{}'", to);
	}

	@Async
	public void sendEmailQuietly(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
		try {
			sendEmail(to, subject, content, isMultipart, isHtml);
		} catch (final Exception e) {
			log.warn("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
		}
	}

	@Async
	public void sendActivationEmail(User user, String baseUrl) {
		log.debug("Sending activation e-mail to '{}'", user.getEmail());
		final Locale locale = Locale.forLanguageTag(user.getLangKey());
		final Context context = new Context(locale);
		context.setVariable("user", user);
		context.setVariable("baseUrl", baseUrl);
		final String content = templateEngine.process("activationEmail", context);
		final String subject = messageSource.getMessage("email.activation.title", null, locale);
		sendEmailQuietly(user.getEmail(), subject, content, false, true);
	}

	@Async
	public void sendPasswordResetMail(User user, String baseUrl) {
		log.debug("Sending password reset e-mail to '{}'", user.getEmail());
		final Locale locale = Locale.forLanguageTag(user.getLangKey());
		final Context context = new Context(locale);
		context.setVariable("user", user);
		context.setVariable("baseUrl", baseUrl);
		final String content = templateEngine.process("passwordResetEmail", context);
		final String subject = messageSource.getMessage("email.reset.title", null, locale);
		sendEmailQuietly(user.getEmail(), subject, content, false, true);
	}

	@Async
	public void setAdhesionExpiringEmail(Adherent adherent) throws MessagingException {
		log.debug("Sending adhesion expring e-mail to '{}'", adherent.getCoordonnees().getEmail());
		final Context context = new Context(Locale.FRENCH);
		context.setVariable("survey", env.getProperty("survey"));
		final String content = templateEngine.process("adhesionExpiringEmail", context);
		final String subject = messageSource.getMessage("email.expiring.title", null, Locale.FRENCH);
		sendEmail(adherent.getCoordonnees().getEmail(), subject, content, false, true);
	}
}
