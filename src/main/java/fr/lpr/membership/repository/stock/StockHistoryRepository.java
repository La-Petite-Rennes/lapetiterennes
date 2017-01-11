package fr.lpr.membership.repository.stock;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import fr.lpr.membership.domain.Article;
import fr.lpr.membership.domain.stock.StockHistory;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {

	Page<StockHistory> findByArticle(Article article, Pageable pageRequest);

}
