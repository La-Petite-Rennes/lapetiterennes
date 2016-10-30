package fr.lpr.membership.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import fr.lpr.membership.domain.Article;

@RestController
@RequestMapping("/api")
public class ArticleResource {

	private List<Article> articles;
	
	@PostConstruct
	public void init() {
		articles = new ArrayList<>(5);
		articles.add(new Article().name("Cassette Route Shimano Ultegra 6800 11 vitesses")
				.price(5399).quantity(8));
		articles.add(new Article().name("Dérailleur arrière Shimano Tiagra 4700 10v")
				.price(3199).quantity(15));
		articles.add(new Article().name("Frein Shimano Dura-Ace 9000").price(11799).quantity(3));
		articles.add(new Article().name("Pneu Route Continental Grand Prix 4000S II - 23c PAIR").price(6999).quantity(25));
	}
	
	@RequestMapping(value="/articles", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<Article> getAll() {
		return articles;
	}
	
	@RequestMapping(value="/articles/{id}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Article> get(@PathVariable Long id) {
		if (id < 0 || id > articles.size()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(articles.get(id.intValue()), HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/articles", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> create(@Valid @RequestBody Article article) throws URISyntaxException {
		this.articles.add(article);
		return ResponseEntity.created(new URI("/api/articles/" + (articles.size()-1))).build();
	}

}
