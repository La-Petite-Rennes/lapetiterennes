package fr.lpr.membership.web.rest;

import static com.google.common.collect.ImmutableList.of;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import fr.lpr.membership.domain.Article;
import fr.lpr.membership.domain.Provider;
import fr.lpr.membership.domain.stock.Reassort;
import fr.lpr.membership.repository.ArticleRepository;
import fr.lpr.membership.repository.ProviderRepository;
import fr.lpr.membership.service.stock.ReassortService;

@RestController
@RequestMapping("/api")
public class ArticleResource {

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private ProviderRepository providerRepository;

	@Autowired
	private ReassortService reassortService;

	// FIXME A Supprimer
	@PostConstruct
	public void init() {
		if (!providerRepository.findAll().isEmpty()) {
			return;
		}

		Provider p1 = new Provider().name("Chain Reaction Cycles");
		Provider p2 = new Provider().name("Probike Shop");
		providerRepository.save(of(p1, p2));

		List<Article> articles = new ArrayList<>(5);
		articles.add(new Article().name("Dérailleur arrière Shimano Tiagra 4700 10v")
				.id(13L).salePrice(3199).quantity(15).provider(p1).unitPrice(2599));
		articles.add(new Article().name("Cassette Route Shimano Ultegra 6800 11 vitesses")
				.id(35L).salePrice(5399).quantity(8).provider(p1).unitPrice(4599));
		articles.add(new Article().id(5L).name("Frein Shimano Dura-Ace 9000").salePrice(11799).quantity(3).provider(p2).unitPrice(11000));
		articles.add(new Article().id(8L).name("Pneu Route Continental Grand Prix 4000S II - 23c PAIR").salePrice(6999).provider(p2).unitPrice(5150).quantity(25));
		articles.add(new Article().id(15L).name("Cable de frein").quantity(47));
		articleRepository.save(articles);
	}

	@RequestMapping(value="/articles", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<Article> getAll() {
		return articleRepository.findAll();
	}

	@RequestMapping(value="/articles/{id}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Article> get(@PathVariable Long id) {
		return Optional.ofNullable(articleRepository.findOne(id))
				.map(a -> new ResponseEntity<>(a, OK))
				.orElse(new ResponseEntity<>(NOT_FOUND));
	}

	@RequestMapping(value = "/articles", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> create(@Valid @RequestBody Article article) throws URISyntaxException {
		Article savedArticle = articleRepository.save(article);
		return ResponseEntity.created(new URI("/api/articles/" + savedArticle.getId())).build();
	}

	@RequestMapping(value="/articles/reassort", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> reassort(List<Reassort> reassorts) {
		reassortService.reassort(reassorts);
		return ResponseEntity.ok().build();
	}

}
