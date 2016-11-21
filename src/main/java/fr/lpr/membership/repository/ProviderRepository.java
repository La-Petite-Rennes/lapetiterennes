package fr.lpr.membership.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.lpr.membership.domain.Provider;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

}
