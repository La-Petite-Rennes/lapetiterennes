package fr.lpr.membership.web.rest.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.domain.Article;
import fr.lpr.membership.domain.sale.Sale;
import fr.lpr.membership.domain.sale.SoldItem;
import fr.lpr.membership.repository.AdherentRepository;
import fr.lpr.membership.repository.ArticleRepository;
import fr.lpr.membership.web.rest.dto.SaleDTO;
import fr.lpr.membership.web.rest.dto.SoldItemDTO;

@Mapper(componentModel = "spring")
public abstract class SaleMapper {

	@Autowired
	private AdherentRepository adherentRepository;

	@Autowired
	private ArticleRepository articleRepository;

	@Mapping(source="date", target="createdAt")
	@Mapping(ignore = true,  target="updatedAt")
	@Mapping(source="adherentId", target="adherent")
	public abstract Sale saleDtoToSale(SaleDTO saleDTO);

	@Mapping(source="createdAt", target="date")
	@Mapping(source="adherent.id", target="adherentId")
	@Mapping(source="adherent.fullName", target="adherentFullName")
	public abstract SaleDTO saleToSaleDto(Sale sale);

	public SoldItem soldItemToStockHistory(SoldItemDTO soldItemDTO) {
		Article article = articleFromId(soldItemDTO.getArticleId());

		SoldItem soldItem = new SoldItem(article, soldItemDTO.getQuantity(), soldItemDTO.getPrice());
		soldItem.setId(soldItemDTO.getId());

		return soldItem;
	}

	public SoldItemDTO soldItemToDto(SoldItem soldItem) {
		SoldItemDTO dto = new SoldItemDTO();
		dto.setId(soldItem.getId());
		dto.setArticleId(soldItem.getArticle().getId());
		dto.setPrice(soldItem.getPrice());
		dto.setQuantity(soldItem.getQuantity());
		dto.setName(soldItem.getArticle().getName());
		return dto;
	}

	public Adherent adherentFromId(Long adherentId) {
        return adherentRepository.getOne(adherentId);
	}

	public Article articleFromId(Long articleId) {
        return articleRepository.getOne(articleId);
	}

}
