package fr.lpr.membership.service.stock;

import fr.lpr.membership.domain.Article;
import fr.lpr.membership.domain.stock.StockHistory;
import fr.lpr.membership.repository.stock.StockHistoryRepository;
import fr.lpr.membership.web.rest.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StockService {

	private final StockHistoryRepository stockHistoryRepository;

	private final ApplicationEventPublisher eventPublisher;

	/**
	 * Utilise un article "Pour réparation".
	 */
	public void forRepairing(Article article) {
		stockHistoryRepository.save(StockHistory.forRepairing(article));
		eventPublisher.publishEvent(StockQuantityChangedEvent.forRepairing(article));
	}

	/**
	 * Recherche de l'historique d'un article.
	 */
	public Page<StockHistory> history(Article article, Integer offset, Integer limit) {
		final Sort sort = new Sort(Direction.DESC, "createdAt");
		final Pageable pageRequest = PaginationUtil.generatePageRequest(offset, limit, sort);

		return stockHistoryRepository.findByArticle(article, pageRequest);
	}
}
