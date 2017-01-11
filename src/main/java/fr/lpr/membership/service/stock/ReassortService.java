package fr.lpr.membership.service.stock;

import static fr.lpr.membership.service.stock.StockQuantityChangedEvent.fromReassort;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.lpr.membership.domain.Article;
import fr.lpr.membership.domain.stock.Reassort;
import fr.lpr.membership.domain.stock.StockHistory;
import fr.lpr.membership.repository.ArticleRepository;
import fr.lpr.membership.repository.stock.StockHistoryRepository;

/**
 * Service de réapprovisionnement du stock.
 */
@Service
public class ReassortService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReassortService.class);

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private StockHistoryRepository stockHistoryRepository;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Transactional
	public void reassort(List<Reassort> reassorts) {
		LOGGER.info("----- Début de réassort -----");

		for (Reassort reassort : reassorts) {
			if (reassort.getQuantity() != 0) {
				Article article = articleRepository.findOne(reassort.getId());

				stockHistoryRepository.save(StockHistory.from(reassort, article));
				eventPublisher.publishEvent(fromReassort(article, reassort.getQuantity()));
				LOGGER.info("Réassort {} '{}' ", reassort.getQuantity(), article.getName());
			}
		}

		LOGGER.info("----- Fin de réassort -----");
	}

}
