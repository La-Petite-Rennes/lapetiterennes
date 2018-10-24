package fr.lpr.membership.service;

import com.google.common.collect.ImmutableSet;
import com.opencsv.CSVReader;
import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.domain.Adhesion;
import fr.lpr.membership.domain.Coordonnees;
import fr.lpr.membership.domain.TypeAdhesion;
import fr.lpr.membership.repository.AdherentRepository;
import lombok.RequiredArgsConstructor;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportService {

	private int id;
	private int name;
	private int street;
	private int street2;
	private int zip;
	private int city;
	private int email;
	private int phone;
	private int typeAdhesion;
	private int dateAdhesion;
	private int dateAdhesionToParse;

	private int compteur;

	private final AdherentRepository adherentRepository;

	public void importCsv(InputStream inputStream) throws IOException {
		final CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		parseHeader(Arrays.asList(reader.readNext()));
		reader.forEach(this::readAdherent);
		reader.close();

		System.out.println("Adhérents sans type : " + compteur);
		compteur = 0;
	}

	private void parseHeader(List<String> readNext) {
		this.id = readNext.indexOf("id");
		this.name = readNext.indexOf("name");
		this.street = readNext.indexOf("street");
		this.street2 = readNext.indexOf("street2");
		this.zip = readNext.indexOf("zip");
		this.city = readNext.indexOf("city");
		this.email = readNext.indexOf("email");
		this.phone = readNext.indexOf("phone");
		this.typeAdhesion = readNext.indexOf("member_lines/membership_id");
		this.dateAdhesion = readNext.indexOf("member_lines/date");
		this.dateAdhesionToParse = readNext.indexOf("member_lines/id");
	}

	private void readAdherent(String[] csv) {
		// Do nothing if no identifier in the CSV file
		if (csv[id].isEmpty()) {
			return;
		}
		// Do nothing if typeAdhesion and dateAdhesion are empty
		if (csv[typeAdhesion].isEmpty()) {
			++compteur;
			return;
		}

		final Coordonnees coordonnees = new Coordonnees();
		coordonnees.setAdresse1(csv[street]);
		coordonnees.setAdresse2(csv[street2]);
		coordonnees.setCodePostal(csv[zip]);
		coordonnees.setVille(csv[city]);
		coordonnees.setEmail(csv[email]);
		coordonnees.setTelephone(csv[phone]);

		final Adhesion adhesion = new Adhesion();
		adhesion.setTypeAdhesion(getTypeAdhesion(csv[typeAdhesion]));
		adhesion.setDateAdhesion(getDateAdhesion(csv[dateAdhesion], csv[dateAdhesionToParse]));

		final Adherent adherent = new Adherent();
		adherent.setPrenom(" ");
		adherent.setNom(csv[name]);
		adherent.setCoordonnees(coordonnees);
		adherent.setAdhesions(ImmutableSet.of(adhesion));

		adherentRepository.save(adherent);
	}

	private TypeAdhesion getTypeAdhesion(String typeAdhesionCsv) {
		if ("[001] Adhésion classique".equals(typeAdhesionCsv)) {
			return TypeAdhesion.Simple;
		} else if ("[002] Adhésion famille".equals(typeAdhesionCsv)) {
			return TypeAdhesion.Famille;
		} else {
			return null;
		}
	}

	private LocalDate getDateAdhesion(String dateAdhesionCsv, String dateAdhesionCsvToParse) {
		if (!dateAdhesionCsv.isEmpty()) {
			return LocalDate.parse(dateAdhesionCsv, ISODateTimeFormat.dateParser());
		} else if (!dateAdhesionCsvToParse.isEmpty()) {
			return LocalDate.parse(dateAdhesionCsvToParse.substring(dateAdhesionCsvToParse.length() - 10), ISODateTimeFormat.dateParser());
		} else {
			return null;
		}
	}
}
