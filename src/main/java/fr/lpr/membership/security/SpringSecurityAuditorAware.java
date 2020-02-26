package fr.lpr.membership.security;

import fr.lpr.membership.config.Constants;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        String userName = SecurityUtils.getCurrentLogin();
        return Optional.of(userName != null ? userName : Constants.SYSTEM_ACCOUNT);
    }
}
