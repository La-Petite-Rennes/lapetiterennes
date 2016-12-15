package fr.lpr.membership.repository.sale;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import fr.lpr.membership.domain.sale.Sale;

public interface SaleRepository extends JpaRepository<Sale, Long>, QueryDslPredicateExecutor<Sale> {

}
