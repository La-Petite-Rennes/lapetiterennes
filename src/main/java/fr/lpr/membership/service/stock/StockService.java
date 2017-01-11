package fr.lpr.membership.service.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.lpr.membership.domain.Article;
import fr.lpr.membership.domain.stock.StockHistory;
import fr.lpr.membership.repository.stock.StockHistoryRepository;
import fr.lpr.membership.web.rest.util.PaginationUtil;

@Service
@Transactional
public class StockService {

	@Autowired
	private StockHistoryRepository stockHistoryRepository;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	/**
	 * Utilise un article "Pour réparation".
	 *
	 * @param articleId
	 */
	public void forRepairing(Article article) {
		stockHistoryRepository.save(StockHistory.forRepairing(article));
		eventPublisher.publishEvent(StockQuantityChangedEvent.forRepairing(article));
	}

	/**
	 * Recherche de l'historique d'un article.
	 *
	 * @param article
	 * @param offset
	 * @param limit
	 * @return
	 */
	public Page<StockHistory> history(Article article, Integer offset, Integer limit) {
		final Sort sort = new Sort(Direction.DESC, "createdAt");
		final Pageable pageRequest = PaginationUtil.generatePageRequest(offset, limit, sort);

		return stockHistoryRepository.findByArticle(article, pageRequest);
	}

}
