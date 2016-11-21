package fr.lpr.membership.service.stock;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.lpr.membership.domain.Article;
import fr.lpr.membership.domain.stock.Reassort;
import fr.lpr.membership.domain.stock.StockHistory;
import fr.lpr.membership.repository.ArticleRepository;
import fr.lpr.membership.repository.stock.StockHistoryRepository;

@Service
public class ReassortService {

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private StockHistoryRepository stockHistoryRepository;

	@Transactional
	public void reassort(List<Reassort> reassorts) {
		for (Reassort reassort : reassorts) {
			// TODO Gérer la quantité d'article avec un événement Spring suite à création d'un StockHistory
			Article article = articleRepository.findOne(reassort.getId());
			article.setQuantity(article.getQuantity() + reassort.getQuantity());

			stockHistoryRepository.save(StockHistory.from(reassort));
		}
	}

}
