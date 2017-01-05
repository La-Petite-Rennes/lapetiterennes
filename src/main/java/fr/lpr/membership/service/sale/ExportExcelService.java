package fr.lpr.membership.service.sale;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.YearMonth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import fr.lpr.membership.domain.Article;
import fr.lpr.membership.domain.TypeAdhesion;
import fr.lpr.membership.repository.ArticleRepository;

@Service
public class ExportExcelService {

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private SaleStatisticsService saleStatisticsService;

	public void export() {
		// Create the Excel file (format XSLX)
		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Statistiques");

			// Get statistics by month
			DateTime from = DateTime.now().withDayOfMonth(1).withMonthOfYear(1);
			SaleStatistics<YearMonth> itemsByMonth = saleStatisticsService.statsByMonths(from);

			List<String> columnNames = getItemNames();

			int currentSheetRow = 0;
			for (YearMonth month : itemsByMonth.getItemsByPeriod().keySet()) {
				Multimap<String, SalableItem> statsByItem = monthlyStatByItem(itemsByMonth.getItemsByPeriod().get(month));

				Row row = sheet.createRow(currentSheetRow);
				row.createCell(0).setCellValue(month.getMonthOfYear());

				for (int index = 0; index != columnNames.size(); ++index) {
					String column = columnNames.get(index);
					int totalPrice = statsByItem.get(column).stream().map(SalableItem::getTotalPrice).reduce(Integer::sum).orElse(0);

					row.createCell(index + 1).setCellValue("€" + (totalPrice / 100) + "." + (totalPrice % 100));
				}

				++currentSheetRow;
			}
		} catch (Exception ex) {
			// FIXME workbook.write(output);
		}
	}

	private List<String> getItemNames() {
		List<String> itemNames = new ArrayList<>();
		itemNames.addAll(Arrays.asList(TypeAdhesion.values()).stream().map(TypeAdhesion::getLabel).collect(Collectors.toList()));
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
