package fr.lpr.membership.web.rest;

import fr.lpr.membership.domain.Provider;
import fr.lpr.membership.repository.ProviderRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProviderResource {

	private final ProviderRepository providerRepository;

	@GetMapping
	@Timed
	public List<Provider> getAll() {
		return providerRepository.findAll();
	}

}
