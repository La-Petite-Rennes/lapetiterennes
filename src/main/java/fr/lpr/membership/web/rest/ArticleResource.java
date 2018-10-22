package fr.lpr.membership.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.lpr.membership.domain.Article;
import fr.lpr.membership.domain.stock.Reassort;
import fr.lpr.membership.domain.stock.StockHistory;
import fr.lpr.membership.repository.ArticleRepository;
import fr.lpr.membership.service.stock.ReassortService;
import fr.lpr.membership.service.stock.StockService;
import fr.lpr.membership.web.rest.dto.StockHistoryDTO;
import fr.lpr.membership.web.rest.dto.mapper.StockMapper;
import fr.lpr.membership.web.rest.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fr.lpr.membership.security.AuthoritiesConstants.WORKSHOP_MANAGER;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ArticleResource {

	private final ArticleRepository articleRepository;

	private final ReassortService reassortService;

	private final StockService stockService;

	private final StockMapper stockMapper;

	@RequestMapping(value="/articles", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<Article> getAll() {
		return articleRepository.findAll(new Sort("name"));
	}

	@RequestMapping(value = "/articles/{id}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Article> get(@PathVariable Long id) {
		return Optional.ofNullable(articleRepository.findOne(id))
				.map(a -> new ResponseEntity<>(a, OK))
				.orElse(new ResponseEntity<>(NOT_FOUND));
	}

	@RequestMapping(value = "/articles", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
	@RolesAllowed(WORKSHOP_MANAGER)
	@Timed
	public ResponseEntity<Void> create(@Valid @RequestBody Article article) throws URISyntaxException {
		Article savedArticle = articleRepository.save(article);
		return ResponseEntity.created(new URI("/api/articles/" + savedArticle.getId())).build();
	}

	@RequestMapping(value = "/articles/reassort", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
	@RolesAllowed(WORKSHOP_MANAGER)
	@Timed
	public ResponseEntity<Void> reassort(@RequestBody List<Reassort> reassorts) {
		reassortService.reassort(reassorts);
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = "/articles/{id}/forRepairing", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
	@RolesAllowed(WORKSHOP_MANAGER)
	@Timed
	public ResponseEntity<Article> forRepairing(@PathVariable(name = "id") Long articleId) {
		Article article = articleRepository.findOne(articleId);
		if (article != null) {
			stockService.forRepairing(article);
			return new ResponseEntity<>(articleRepository.findOne(articleId), OK);
		} else {
			return new ResponseEntity<>(NOT_FOUND);
		}
	}

	@RequestMapping(value = "/articles/{id}/history", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<StockHistoryDTO>> stockHistory(@PathVariable(name = "id") Long articleId,
			@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException {
		// Find article
		Article article = articleRepository.findOne(articleId);
		if (article == null) {
			return new ResponseEntity<>(NOT_FOUND);
		}

		Page<StockHistory> page = stockService.history(article, offset, limit);

		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/articles/" + articleId + "/history", offset, limit);
		return new ResponseEntity<>(page.getContent().stream().map(stockMapper::stockHistoryToDto).collect(Collectors.toList()), headers, HttpStatus.OK);
	}

}
