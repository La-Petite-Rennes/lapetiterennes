package fr.lpr.membership.service;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.opencsv.CSVWriter;
import com.opencsv.bean.BeanToCsv;
import com.opencsv.bean.ColumnPositionMappingStrategy;

import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.repository.AdherentRepository;
import fr.lpr.membership.web.rest.util.PaginationUtil;

@Service
public class ExportService {

	public static final String CSV = "csv";
	public static final String XML = "xml";
	public static final String JSON = "json";

	private static final Map<String, BiConsumer<Adherent, AdherentDto>> DTO_MAPPER = ImmutableMap.<String, BiConsumer<Adherent, AdherentDto>> builder()
			.put("id", (a, dto) -> dto.id = a.getId()).put("nom", (a, dto) -> dto.nom = a.getNom()).put("prenom", (a, dto) -> dto.prenom = a.getPrenom())
			.put("estBenevole", (a, dto) -> dto.estBenevole = a.getBenevole())
			.put("adresse", (a, dto) -> dto.adresse = a.getCoordonnees().getAdresseComplete())
			.put("codePostal", (a, dto) -> dto.codePostal = a.getCoordonnees().getCodePostal())
			.put("ville", (a, dto) -> dto.ville = a.getCoordonnees().getVille()).put("email", (a, dto) -> dto.email = a.getCoordonnees().getEmail())
			.put("telephone", (a, dto) -> dto.telephone = a.getCoordonnees().getTelephone()).put("adhesions", (a, dto) -> a.getAdhesions()).build();

	@Autowired
	private AdherentRepository adherentRepository;

	public void export(String format, List<String> properties, OutputStream outputStream) {
		Page<Adherent> page = null;
		do {
			page = adherentRepository.findAll(page == null ? PaginationUtil.generatePageRequest(0, 100) : page.nextPageable());
			final List<AdherentDto> dtos = page.getContent().stream().map(ad -> {
				final AdherentDto adherentDto = new AdherentDto();
				properties.stream().forEach(p -> DTO_MAPPER.get(p).accept(ad, adherentDto));
				return adherentDto;
			}).collect(Collectors.toList());

			final CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(outputStream), ';');
			csvWriter.writeNext(new String[] { "ID", "Nom", "Prénom", "Adresse", "Code Postal", "Ville", "Date de dernière adhésion", "Email", "Téléphone" });

			final ColumnPositionMappingStrategy<AdherentDto> strat = new ColumnPositionMappingStrategy<>();
			strat.setType(AdherentDto.class);
			final String[] columns = properties.stream().toArray(size -> new String[size]);
			strat.setColumnMapping(columns);

			final BeanToCsv<AdherentDto> csv = new BeanToCsv<>();
			csv.write(strat, csvWriter, dtos);
		} while (page.hasNext());
	}

	private static class AdherentDto {

		public Long id;
		public String nom;
		public String prenom;
		public boolean estBenevole;
		public String adresse;
		public String codePostal;
		public String ville;
		public String email;
		public String telephone;

	}

	private static interface Writer {
		void write(Collection<AdherentDto> adherents, OutputStream output);
	}

	private static class CsvWriter implements Writer {

		@Override
		public void write(Collection<AdherentDto> adherents, OutputStream output) {
			// TODO Auto-generated method stub

		}

	}

}
