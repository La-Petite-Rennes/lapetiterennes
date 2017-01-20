package fr.lpr.membership.web.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import fr.lpr.membership.domain.Provider;
import fr.lpr.membership.repository.ProviderRepository;

@RestController
@RequestMapping("/api/providers")
public class ProviderResource {

	@Autowired
	private ProviderRepository providerRepository;

	@RequestMapping(method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<Provider> getAll() {
		return providerRepository.findAll();
	}

}
