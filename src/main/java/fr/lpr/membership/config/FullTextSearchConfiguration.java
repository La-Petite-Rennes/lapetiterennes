package fr.lpr.membership.config;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(value = DatabaseConfiguration.class)
public class FullTextSearchConfiguration {

	@Autowired
	private EntityManager entityManager;

	@PostConstruct
	public void buildIndex() throws InterruptedException {
		final FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		fullTextEntityManager.createIndexer().startAndWait();
	}

}
