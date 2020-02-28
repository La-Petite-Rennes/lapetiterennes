package fr.lpr.membership.service.adhesion;

import com.google.common.base.Strings;
import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.domain.StatutAdhesion;
import fr.lpr.membership.domain.TypeAdhesion;
import fr.lpr.membership.repository.AdherentRepository;
import fr.lpr.membership.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDate.now;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
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
			page = adherentRepository.findAll(page == null ? PageRequest.of(0, 100, Sort.by("id")) : page.nextPageable());

			adherents.addAll(page.getContent().stream()
                .filter(ad -> ad.getStatutAdhesion() == StatutAdhesion.ORANGE)
                .filter(ad -> ad.lastAdhesion().map(adhesion -> adhesion.getTypeAdhesion() != TypeAdhesion.Mensuelle).orElse(false))
				.filter(ad -> !Strings.isNullOrEmpty(ad.getCoordonnees().getEmail()))
				.filter(ad -> ad.getReminderEmail() == null || ad.getReminderEmail().isBefore(now().minusMonths(1)))
				.collect(Collectors.toList()));
		} while (page.hasNext());

		// Send mail for each adherents
		adherents.forEach(ad -> {
			try {
				sendReminderMail(ad);
			} catch (final Exception ex) {
				log.error("E-mail could not be sent to adherent '{}', exception is: {}", ad.getPrenom() + " " + ad.getNom(), ex.getMessage());
			}
		});

		log.info("{} emails de rappel de cotisation envoyés", adherents.size());
	}

    @Scheduled(cron = "0 0 9 * * *", zone = "GMT")
    public void adhesionExpired() {
        log.info("Démarrage du batch d'envoi d'email des adhérents dont l'adhésion vient d'expirer");

        // Get Adherents
        final List<Adherent> adherents = new ArrayList<>();

        Page<Adherent> page = null;
        do {
            page = adherentRepository.findAll(page == null ? PageRequest.of(0, 100, Sort.by("id")) : page.nextPageable());

            adherents.addAll(page.getContent().stream()
                .filter(ad -> ad.getStatutAdhesion() == StatutAdhesion.RED)
				.filter(ad -> !Strings.isNullOrEmpty(ad.getCoordonnees().getEmail()))
				.filter(ad -> ad.lastAdhesion().map(adh -> adh.getDateFinAdhesion().isAfter(now().minusDays(1))).orElse(false))
				.collect(Collectors.toList()));
        } while (page.hasNext());

        // Send mail for each adherents
        adherents.forEach(ad -> {
            try {
                mailService.sendAdhesionExpiredEmail(ad);
            } catch (final Exception ex) {
                log.error("E-mail could not be sent to adherent '{}', exception is: {}", ad.getPrenom() + " " + ad.getNom(), ex.getMessage());
            }
        });

        log.info("{} emails de fin d'adhésion envoyés", adherents.size());
    }

	public void sendReminderMail(Adherent adherent) throws MessagingException {
		mailService.sendAdhesionExpiringEmail(adherent);
		adherent.setReminderEmail(now());
		adherentRepository.save(adherent);
	}

    public Adherent createAdherent(Adherent adherent) throws MessagingException {
        Adherent created = adherentRepository.save(adherent);
        mailService.sendFirstAdhesionEmail(created);
        return created;
    }
}
