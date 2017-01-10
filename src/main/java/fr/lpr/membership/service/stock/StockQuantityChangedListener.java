package fr.lpr.membership.service.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import fr.lpr.membership.repository.ArticleRepository;

@Component
public class StockQuantityChangedListener {

	@Autowired
	private ArticleRepository articleRepository;

	@EventListener
	public void handleStockQuantityChanged(StockQuantityChangedEvent event) {
		articleRepository.updateQuantity(event.getArticle().getId(), event.getQuantity());
	}

}
