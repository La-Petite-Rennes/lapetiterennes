package fr.lpr.membership.web.rest.dto.mapper;

import org.mapstruct.Mapper;

import fr.lpr.membership.domain.stock.StockEvent;
import fr.lpr.membership.domain.stock.StockHistory;
import fr.lpr.membership.web.rest.dto.StockHistoryDTO;

@Mapper
public abstract class StockMapper {

	public abstract StockHistoryDTO stockHistoryToDto(StockHistory stockHistory);

	public String stockEventToString(StockEvent stockEvent) {
		return stockEvent.getLabel();
	}

}
