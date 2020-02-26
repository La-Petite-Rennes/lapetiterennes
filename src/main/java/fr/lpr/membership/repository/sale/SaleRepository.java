package fr.lpr.membership.repository.sale;

import fr.lpr.membership.domain.sale.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface SaleRepository extends JpaRepository<Sale, Long>, QuerydslPredicateExecutor<Sale> {

}
