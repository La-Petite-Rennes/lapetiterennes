package fr.lpr.membership.service.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.lpr.membership.domain.Article;
import fr.lpr.membership.domain.stock.StockHistory;
import fr.lpr.membership.repository.stock.StockHistoryRepository;

@Service
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
	@Transactional
	public void forRepairing(Article article) {
		stockHistoryRepository.save(StockHistory.forRepairing(article));
		eventPublisher.publishEvent(StockQuantityChangedEvent.forRepairing(article));
	}

}
