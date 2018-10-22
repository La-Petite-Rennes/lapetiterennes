package fr.lpr.membership.service.sale;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import fr.lpr.membership.domain.Article;
import fr.lpr.membership.domain.TypeAdhesion;
import fr.lpr.membership.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.YearMonth;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExportExcelService {

	private final ArticleRepository articleRepository;

	private final SaleStatisticsService saleStatisticsService;

	public void export(OutputStream outputStream) {
		// Create the Excel file (format XSLX)
		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Statistiques");

			// Get statistics by month
			DateTime from = DateTime.now().minusYears(1).withDayOfMonth(1).withMonthOfYear(1);
			SaleStatistics<YearMonth> itemsByMonth = saleStatisticsService.statsByMonths(from);

			List<String> columnNames = getItemNames();
			int currentSheetRow = 0;

			// Write header
			Row header = sheet.createRow(currentSheetRow);
			for (int index = 0; index != columnNames.size(); ++index) {
				String column = columnNames.get(index);
				header.createCell(index + 1).setCellValue(column);
			}
			++ currentSheetRow;

			// Write a row
			for (YearMonth month : itemsByMonth.getItemsByPeriod().keySet()) {
				Multimap<String, SalableItem> statsByItem = monthlyStatByItem(itemsByMonth.getItemsByPeriod().get(month));

				Row row = sheet.createRow(currentSheetRow);
				row.createCell(0).setCellValue(month.toString("MMMM yyyy"));

				for (int index = 0; index != columnNames.size(); ++index) {
					String column = columnNames.get(index);
					int totalPrice = statsByItem.get(column).stream().map(SalableItem::getTotalPrice).reduce(Integer::sum).orElse(0);

					row.createCell(index + 1).setCellValue("€" + (totalPrice / 100) + "." + (totalPrice % 100));
				}

				++currentSheetRow;
			}

			workbook.write(outputStream);
		} catch (IOException ex) {
			log.error("Failed to export sale statistics", ex);
		}
	}

	private List<String> getItemNames() {
		List<String> itemNames = new ArrayList<>();
		itemNames.addAll(Arrays.stream(TypeAdhesion.values()).map(TypeAdhesion::getLabel).collect(Collectors.toList()));
		itemNames.addAll(articleRepository.findAll().stream().map(Article::getName).collect(Collectors.toList()));

		itemNames.sort(Comparator.naturalOrder());
		return itemNames;
	}

	private Multimap<String, SalableItem> monthlyStatByItem(Collection<SalableItem> items) {
		Multimap<String, SalableItem> statsByItem = HashMultimap.create();
		for (SalableItem item : items) {
			statsByItem.put(item.getName(), item);
		}
		return statsByItem;
	}

}
