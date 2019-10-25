package fr.lpr.membership.service;

import fr.lpr.membership.config.audit.AuditEventConverter;
import fr.lpr.membership.domain.PersistentAuditEvent;
import fr.lpr.membership.repository.PersistenceAuditEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
@RequiredArgsConstructor
public class AuditEventService {

	private final PersistenceAuditEventRepository persistenceAuditEventRepository;

	private final AuditEventConverter auditEventConverter;

	public List<AuditEvent> findAll() {
		return auditEventConverter.convertToAuditEvent(persistenceAuditEventRepository.findAll());
	}

	public List<AuditEvent> findByDates(LocalDateTime fromDate, LocalDateTime toDate) {
		final List<PersistentAuditEvent> persistentAuditEvents = persistenceAuditEventRepository.findAllByAuditEventDateBetween(fromDate, toDate);

		return auditEventConverter.convertToAuditEvent(persistentAuditEvents);
	}
}
