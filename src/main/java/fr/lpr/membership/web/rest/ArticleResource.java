package fr.lpr.membership.web.rest;

import fr.lpr.membership.domain.Article;
import fr.lpr.membership.domain.stock.Reassort;
import fr.lpr.membership.domain.stock.StockHistory;
import fr.lpr.membership.repository.ArticleRepository;
import fr.lpr.membership.security.AuthoritiesConstants;
import fr.lpr.membership.service.stock.ReassortService;
import fr.lpr.membership.service.stock.StockService;
import fr.lpr.membership.web.rest.dto.StockHistoryDTO;
import fr.lpr.membership.web.rest.dto.mapper.StockMapper;
import fr.lpr.membership.web.rest.util.PaginationUtil;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fr.lpr.membership.security.AuthoritiesConstants.WORKSHOP_MANAGER;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleResource {

	private final ArticleRepository articleRepository;

	private final ReassortService reassortService;

	private final StockService stockService;

	private final StockMapper stockMapper;

    @GetMapping
	@Timed
	public List<Article> getAll() {
		return articleRepository.findAll(Sort.by("name"));
	}

	@GetMapping("/{id}")
	@Timed
	public ResponseEntity<Article> get(@PathVariable Long id) {
		return articleRepository.findById(id)
				.map(a -> new ResponseEntity<>(a, OK))
				.orElse(new ResponseEntity<>(NOT_FOUND));
	}

	@PostMapping
	@RolesAllowed(WORKSHOP_MANAGER)
	@Timed
	public ResponseEntity<Void> create(@Validated @RequestBody Article article) throws URISyntaxException {
		Article savedArticle = articleRepository.save(article);
		return ResponseEntity.created(new URI("/api/articles/" + savedArticle.getId())).build();
	}

	@PostMapping("/reassort")
	@RolesAllowed(WORKSHOP_MANAGER)
	@Timed
	public ResponseEntity<Void> reassort(@RequestBody List<Reassort> reassorts) {
		reassortService.reassort(reassorts);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{id}/forRepairing")
	@RolesAllowed(WORKSHOP_MANAGER)
	@Timed
	public ResponseEntity<Article> forRepairing(@PathVariable(name = "id") Long articleId) {
		Optional<Article> article = articleRepository.findById(articleId);
		if (article.isPresent()) {
			stockService.forRepairing(article.get());
			return new ResponseEntity<>(article.get(), OK);
		} else {
			return new ResponseEntity<>(NOT_FOUND);
		}
	}

	@GetMapping("/{id}/history")
	public ResponseEntity<List<StockHistoryDTO>> stockHistory(
	    @PathVariable(name = "id") Long articleId,
		@RequestParam(value = "page", required = false) Integer offset,
		@RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException
    {
		// Find article
		Optional<Article> article = articleRepository.findById(articleId);
		if (article.isEmpty()) {
			return new ResponseEntity<>(NOT_FOUND);
		}

		Page<StockHistory> page = stockService.history(article.get(), offset, limit);

		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/articles/" + articleId + "/history", offset, limit);
		return new ResponseEntity<>(page.getContent().stream().map(stockMapper::stockHistoryToDto).collect(Collectors.toList()), headers, HttpStatus.OK);
	}

	@DeleteMapping("/{articleId}")
	@RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.WORKSHOP_MANAGER})
    public void delete(@PathVariable Long articleId) {
        articleRepository.delete(articleId);
    }
}
