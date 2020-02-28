package fr.lpr.membership.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.opencsv.CSVWriter;
import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.domain.StatutAdhesion;
import fr.lpr.membership.domain.TypeAdhesion;
import fr.lpr.membership.repository.AdherentRepository;
import fr.lpr.membership.service.exception.ExportException;
import fr.lpr.membership.web.rest.dto.ExportRequest.AdhesionState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportService {

    public static final String CSV = "csv";
    public static final String XML = "xml";
    public static final String JSON = "json";

    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    private static final Map<String, BiConsumer<Adherent, AdherentDto>> DTO_MAPPER = ImmutableMap.<String, BiConsumer<Adherent, AdherentDto>>builder()
        .put("id", (a, dto) -> dto.id = a.getId()).put("nom", (a, dto) -> dto.nom = a.getNom()).put("prenom", (a, dto) -> dto.prenom = a.getPrenom())
        .put("estBenevole", (a, dto) -> dto.estBenevole = a.getBenevole())
        .put("adresse", (a, dto) -> dto.adresse = a.getCoordonnees().getAdresseComplete())
        .put("codePostal", (a, dto) -> dto.codePostal = a.getCoordonnees().getCodePostal())
        .put("ville", (a, dto) -> dto.ville = a.getCoordonnees().getVille())
        .put("email", (a, dto) -> dto.email = a.getCoordonnees().getEmail())
        .put("telephone", (a, dto) -> dto.telephone = a.getCoordonnees().getTelephone()).put("adhesions", (a, dto) -> {
            dto.adhesions = a.getAdhesions().stream().map(ad -> new AdhesionDto(ad.getTypeAdhesion(), ad.getDateAdhesion())).collect(Collectors.toList());
            dto.lastAdhesion = a.getLastAdhesion();
        }).build();

	private final AdherentRepository adherentRepository;

	private final ObjectMapper objectMapper;

    /**
     * Export adherents.
     *
     * @param format        the format (json, xml, csv)
     * @param properties    the properties to export
     * @param adhesionState export adherents with this specified adhesion state
     * @param response      the http response
     */
    public void export(final String format, final List<String> properties, final AdhesionState adhesionState, final HttpServletResponse response) {
        try {
            final List<AdherentDto> adherents = new ArrayList<>();

            Page<Adherent> page = null;
            do {
                page = adherentRepository.findAll(page == null ? PageRequest.of(0, 100, Sort.by("id")) : page.nextPageable());

                adherents.addAll(page.getContent().stream().filter(ad -> filterAdhesionState(ad, adhesionState)).map(ad -> mapDto(ad, properties))
                    .collect(Collectors.toList()));
            } while (page.hasNext());

            switch (format) {
                case XML:
                    exportXml(adherents, response);
                    break;
                case JSON:
                    exportJson(adherents, response);
                    break;
                default:
                    exportCsv(adherents, response);
            }
        } catch (IOException | JAXBException ex) {
            throw new ExportException("Export failed", ex);
        }
    }

	private AdherentDto mapDto(Adherent adherent, final List<String> properties) {
		final AdherentDto adherentDto = new AdherentDto();
		properties.forEach(p -> DTO_MAPPER.get(p).accept(adherent, adherentDto));
		return adherentDto;
	}

    private boolean filterAdhesionState(Adherent ad, AdhesionState adhesionState) {
        if (adhesionState == AdhesionState.all) {
            return true;
        } else if (adhesionState == AdhesionState.valid) {
            return ad.getStatutAdhesion() == StatutAdhesion.GREEN;
        } else if (adhesionState == AdhesionState.expiring) {
            return ad.getStatutAdhesion() == StatutAdhesion.ORANGE;
        } else if (adhesionState == AdhesionState.expired) {
            return ad.getStatutAdhesion() == StatutAdhesion.RED;
        } else if (adhesionState == AdhesionState.recently_expired) {
            final LocalDate lastAdhesion = ad.getLastAdhesion();
            if (lastAdhesion != null) {
                return ad.getStatutAdhesion() == StatutAdhesion.RED && ad.getLastAdhesion().plusYears(1).isAfter(LocalDate.now().minusMonths(1));
            } else {
                return false;
            }
        }
        return true;
    }

    private void exportCsv(List<AdherentDto> dtos, HttpServletResponse response) throws IOException {
        response.setHeader(CONTENT_TYPE_HEADER, "text/csv");

        try (CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(response.getOutputStream()), ';')) {
            csvWriter.writeNext(new String[]{"ID", "Nom", "Prénom", "Adresse", "Code Postal", "Ville", "Date de dernière adhésion", "Email", "Téléphone"});

            dtos.forEach(a -> csvWriter.writeNext(new String[]{a.id.toString(), a.nom, a.prenom, a.adresse, a.codePostal, a.ville, a.formatLastAdhesion(),
                a.email, a.telephone}));
        }
    }

    private void exportXml(List<AdherentDto> dtos, HttpServletResponse response) throws JAXBException, IOException {
        response.setHeader(CONTENT_TYPE_HEADER, "text/xml");

        final JAXBContext context = JAXBContext.newInstance(Adherents.class);
        final Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        m.marshal(new Adherents(dtos), response.getOutputStream());
    }

    private void exportJson(List<AdherentDto> dtos, HttpServletResponse response) throws IOException {
        response.setHeader(CONTENT_TYPE_HEADER, "application/json");
        objectMapper.writeValue(response.getOutputStream(), dtos);
    }

    @Getter
    @Setter
    @JsonInclude(Include.NON_NULL)
    static class AdherentDto implements Serializable {

        private Long id;
        private String nom;
        private String prenom;
        private Boolean estBenevole;
        private String adresse;
        private String codePostal;
        private String ville;
        private String email;
        private String telephone;
        private List<AdhesionDto> adhesions;
        @JsonIgnore
        @XmlTransient
        private LocalDate lastAdhesion;

        public String formatLastAdhesion() {
            if (lastAdhesion != null) {
                return lastAdhesion.format(DateTimeFormatter.ISO_LOCAL_DATE);
            }
            return null;
        }
    }

    @Getter
    @Setter
    static class AdhesionDto {

        private TypeAdhesion typeAdhesion;

        private LocalDate dateAdhesion;

        public AdhesionDto(TypeAdhesion typeAdhesion, LocalDate dateAdhesion) {
            super();
            this.typeAdhesion = typeAdhesion;
            this.dateAdhesion = dateAdhesion;
        }
    }

    @XmlRootElement
    static class Adherents implements Serializable {

        private static final long serialVersionUID = 1L;

        private List<AdherentDto> adherent;

        public Adherents(List<AdherentDto> adherent) {
            super();
            this.adherent = adherent;
        }

        public List<AdherentDto> getAdherent() {
            return adherent;
        }

        public void setAdherent(List<AdherentDto> adherent) {
            this.adherent = adherent;
        }
    }
}
