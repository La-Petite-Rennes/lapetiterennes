package fr.lpr.membership.service.sale;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.lpr.membership.domain.sale.Sale;
import fr.lpr.membership.repository.sale.SaleRepository;

@Service
public class SaleService {

	@Autowired
	private SaleRepository saleRepository;

	@Transactional
	public Sale newSale(Sale sale) {
		sale.updatedAt(sale.getCreatedAt());
		return saleRepository.save(sale);
	}

}
