package fr.lpr.membership.web.rest;

import fr.lpr.membership.security.AuthoritiesConstants;
import fr.lpr.membership.service.AuditEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for getting the audit events.
 */
@RestController
@RequestMapping("/api/audits")
@RequiredArgsConstructor
public class AuditResource {

    private final AuditEventService auditEventService;

    @GetMapping(value = "/all")
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public List<AuditEvent> findAll() {
        return auditEventService.findAll();
    }

    @GetMapping(value = "/byDates")
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public List<AuditEvent> findByDates(
        @RequestParam(value = "fromDate") LocalDateTime fromDate,
        @RequestParam(value = "toDate") LocalDateTime toDate)
    {
        return auditEventService.findByDates(fromDate, toDate);
    }
}
