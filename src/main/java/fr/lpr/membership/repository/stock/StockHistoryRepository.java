package fr.lpr.membership.repository.stock;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.lpr.membership.domain.stock.StockHistory;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {

}
