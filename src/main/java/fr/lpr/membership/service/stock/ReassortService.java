package fr.lpr.membership.service.stock;

import fr.lpr.membership.domain.Article;
import fr.lpr.membership.domain.stock.Reassort;
import fr.lpr.membership.domain.stock.StockHistory;
import fr.lpr.membership.repository.ArticleRepository;
import fr.lpr.membership.repository.stock.StockHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static fr.lpr.membership.service.stock.StockQuantityChangedEvent.fromReassort;

/**
 * Service de réapprovisionnement du stock.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReassortService {

	private final ArticleRepository articleRepository;

	private final StockHistoryRepository stockHistoryRepository;

	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public void reassort(List<Reassort> reassorts) {
		log.info("----- Début de réassort -----");

		for (Reassort reassort : reassorts) {
			if (reassort.getQuantity() != 0) {
				Article article = articleRepository.findOne(reassort.getId());

				stockHistoryRepository.save(StockHistory.from(reassort, article));
				eventPublisher.publishEvent(fromReassort(article, reassort.getQuantity()));
				log.info("Réassort {} '{}' ", reassort.getQuantity(), article.getName());
			}
		}

		log.info("----- Fin de réassort -----");
	}
}
