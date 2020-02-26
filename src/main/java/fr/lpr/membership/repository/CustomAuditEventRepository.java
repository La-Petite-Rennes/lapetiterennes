package fr.lpr.membership.repository;

import fr.lpr.membership.config.audit.AuditEventConverter;
import fr.lpr.membership.domain.PersistentAuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

/**
 * Wraps an implementation of Spring Boot's AuditEventRepository.
 */
@Repository
@RequiredArgsConstructor
public class CustomAuditEventRepository {

    private final PersistenceAuditEventRepository persistenceAuditEventRepository;

    @Bean
    public AuditEventRepository auditEventRepository() {
        return new AuditEventRepository() {

            @Inject
            private AuditEventConverter auditEventConverter;

            @Override
            @Transactional(propagation = Propagation.REQUIRES_NEW)
            public void add(AuditEvent event) {
                PersistentAuditEvent persistentAuditEvent = new PersistentAuditEvent();
                persistentAuditEvent.setPrincipal(event.getPrincipal());
                persistentAuditEvent.setAuditEventType(event.getType());
                persistentAuditEvent.setAuditEventDate(event.getTimestamp().atZone(ZoneId.systemDefault()).toLocalDateTime());
                persistentAuditEvent.setData(auditEventConverter.convertDataToStrings(event.getData()));

                persistenceAuditEventRepository.save(persistentAuditEvent);
            }

            @Override
            public List<AuditEvent> find(String principal, Instant after, String type) {
                Iterable<PersistentAuditEvent> persistentAuditEvents;
                if (principal == null && after == null) {
                    persistentAuditEvents = persistenceAuditEventRepository.findAll();
                } else if (after == null) {
                    persistentAuditEvents = persistenceAuditEventRepository.findByPrincipal(principal);
                } else {
                    persistentAuditEvents =
                        persistenceAuditEventRepository.findByPrincipalAndAuditEventDateAfter(principal, after.atZone(ZoneId.systemDefault()).toLocalDateTime());
                }
                return auditEventConverter.convertToAuditEvent(persistentAuditEvents);
            }
        };
    }
}
