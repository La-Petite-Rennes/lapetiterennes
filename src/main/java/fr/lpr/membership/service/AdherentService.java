package fr.lpr.membership.service;

import com.google.common.base.Strings;
import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.domain.StatutAdhesion;
import fr.lpr.membership.repository.AdherentRepository;
import fr.lpr.membership.web.rest.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author glebreton
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdherentService {

	private final AdherentRepository adherentRepository;

	private final MailService mailService;

	@Scheduled(cron = "0 0 9 * * *", zone = "GMT")
	public void remindAdhesionExpiring() {
		log.info("Démarrage du batch d'envoi d'email de rappel de cotisation");

		// Get Adherents
		final List<Adherent> adherents = new ArrayList<>();

		Page<Adherent> page = null;
		do {
			page = adherentRepository.findAll(page == null ? PaginationUtil.generatePageRequest(0, 100) : page.nextPageable());

			adherents.addAll(page.getContent().stream()
                .filter(ad -> ad.getStatutAdhesion() == StatutAdhesion.ORANGE)
				.filter(ad -> !Strings.isNullOrEmpty(ad.getCoordonnees().getEmail()))
				.filter(ad -> ad.getReminderEmail() == null || ad.getReminderEmail().isBefore(LocalDate.now().minusMonths(1)))
				.collect(Collectors.toList()));
		} while (page.hasNext());

		// Send mail for each adherents
		adherents.forEach(ad -> {
			try {
				sendMail(ad);
			} catch (final Exception ex) {
				log.error("E-mail could not be sent to adherent '{}', exception is: {}", ad.getPrenom() + " " + ad.getNom(), ex.getMessage());
			}
		});

		log.info("{} emails de rappel de cotisation envoyés", adherents.size());
	}

	@Transactional
	public void sendMail(Adherent adherent) throws MessagingException {
		mailService.sendAdhesionExpiringEmail(adherent);
		adherent.setReminderEmail(LocalDate.now());
		adherentRepository.save(adherent);
	}
}
