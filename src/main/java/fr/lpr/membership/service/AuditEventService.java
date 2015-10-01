package fr.lpr.membership.service;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDateTime;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.lpr.membership.config.audit.AuditEventConverter;
import fr.lpr.membership.domain.PersistentAuditEvent;
import fr.lpr.membership.repository.PersistenceAuditEventRepository;

/**
 * <p>
 * Service for managing audit events.
 * </p>
 * <p>
 * This is the default implementation to support SpringBoot Actuator AuditEventRepository
 * </p>
 */
@Service
@Transactional
public class AuditEventService {

	@Inject
	private PersistenceAuditEventRepository persistenceAuditEventRepository;

	@Inject
	private AuditEventConverter auditEventConverter;

	public List<AuditEvent> findAll() {
		return auditEventConverter.convertToAuditEvent(persistenceAuditEventRepository.findAll());
	}

	public List<AuditEvent> findByDates(LocalDateTime fromDate, LocalDateTime toDate) {
		final List<PersistentAuditEvent> persistentAuditEvents = persistenceAuditEventRepository.findAllByAuditEventDateBetween(fromDate, toDate);

		return auditEventConverter.convertToAuditEvent(persistentAuditEvents);
	}
}
