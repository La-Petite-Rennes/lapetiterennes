package fr.lpr.membership.web.rest.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.domain.Article;
import fr.lpr.membership.domain.sale.Sale;
import fr.lpr.membership.domain.stock.StockHistory;
import fr.lpr.membership.repository.AdherentRepository;
import fr.lpr.membership.repository.ArticleRepository;
import fr.lpr.membership.web.rest.dto.SaleDTO;
import fr.lpr.membership.web.rest.dto.SoldItemDTO;

@Mapper
public abstract class SaleMapper {

	@Autowired
	private AdherentRepository adherentRepository;

	@Autowired
	private ArticleRepository articleRepository;

	@Mapping(source="date", target="createdAt")
	@Mapping(source="adherentId", target="adherent")
	public abstract Sale saleDtoToSale(SaleDTO saleDTO);

	public StockHistory soldItemToStockHistory(SoldItemDTO soldItemDTO) {
		Article article = articleFromId(soldItemDTO.getId());
		return StockHistory.forSale(article, soldItemDTO.getQuantity(), soldItemDTO.getPrice());
	}

	public Adherent adherentFromId(Long adherentId) {
		Adherent adherent = adherentRepository.findOne(adherentId);
		if (adherent == null) {
			// FIXME Throws Exception
		}
		return adherent;
	}

	public Article articleFromId(Long articleId) {
		Article article = articleRepository.findOne(articleId);
		if (article == null) {
			// FIXME Throws Exception
		}
		return article;
	}

}
