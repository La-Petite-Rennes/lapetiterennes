package fr.lpr.membership.web.rest.dto.mapper;

import org.mapstruct.Mapper;

import fr.lpr.membership.domain.stock.StockHistory;
import fr.lpr.membership.web.rest.dto.StockHistoryDTO;

@Mapper
public interface StockMapper {

	StockHistoryDTO stockHistoryToDto(StockHistory stockHistory);

}
