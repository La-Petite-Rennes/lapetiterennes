package fr.lpr.membership.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

import fr.lpr.membership.Application;
import fr.lpr.membership.domain.Adherent;
import fr.lpr.membership.domain.Adhesion;
import fr.lpr.membership.domain.Coordonnees;
import fr.lpr.membership.domain.TypeAdhesion;
import fr.lpr.membership.repository.AdherentRepository;
import fr.lpr.membership.repository.SearchAdherentRepository;

/**
 * Test class for the AdherentResource REST controller.
 *
 * @see AdherentResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class AdherentResourceTest {

	private static final String DEFAULT_PRENOM = "SAMPLE_TEXT";
	private static final String UPDATED_PRENOM = "UPDATED_TEXT";
	private static final String DEFAULT_NOM = "SAMPLE_TEXT";
	private static final String UPDATED_NOM = "UPDATED_TEXT";

	private static final Boolean DEFAULT_BENEVOLE = false;
	private static final Boolean UPDATED_BENEVOLE = true;
	private static final String DEFAULT_REMARQUE_BENEVOLAT = "SAMPLE_TEXT";
	private static final String UPDATED_REMARQUE_BENEVOLAT = "UPDATED_TEXT";
	private static final String DEFAULT_AUTRE_REMARQUE = "SAMPLE_TEXT";
	private static final String UPDATED_AUTRE_REMARQUE = "UPDATED_TEXT";

	@Inject
	private AdherentRepository adherentRepository;

	@Inject
	private SearchAdherentRepository searchAdherentRepository;

	@Inject
	private EntityManager entityManager;

	private MockMvc restAdherentMockMvc;

	private Adherent adherent;

	@PostConstruct
	public void setup() {
		MockitoAnnotations.initMocks(this);
		final AdherentResource adherentResource = new AdherentResource();
		ReflectionTestUtils.setField(adherentResource, "adherentRepository", adherentRepository);
		ReflectionTestUtils.setField(adherentResource, "searchAdherentRepository", searchAdherentRepository);
		this.restAdherentMockMvc = MockMvcBuilders.standaloneSetup(adherentResource).build();
	}

	@Before
	public void initTest() {
		adherent = new Adherent();
		adherent.setPrenom(DEFAULT_PRENOM);
		adherent.setNom(DEFAULT_NOM);
		adherent.setBenevole(DEFAULT_BENEVOLE);
		adherent.setRemarqueBenevolat(DEFAULT_REMARQUE_BENEVOLAT);
		adherent.setAutreRemarque(DEFAULT_AUTRE_REMARQUE);
	}

	@Test
	@Transactional
	public void createAdherent() throws Exception {
		final int databaseSizeBeforeCreate = adherentRepository.findAll().size();

		// Create the Adherent
		restAdherentMockMvc.perform(post("/api/adherents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(adherent)))
		.andExpect(status().isCreated());

		// Validate the Adherent in the database
		final List<Adherent> adherents = adherentRepository.findAll();
		assertThat(adherents).hasSize(databaseSizeBeforeCreate + 1);
		final Adherent testAdherent = adherents.get(adherents.size() - 1);
		assertThat(testAdherent.getPrenom()).isEqualTo(DEFAULT_PRENOM);
		assertThat(testAdherent.getNom()).isEqualTo(DEFAULT_NOM);
		assertThat(testAdherent.getBenevole()).isEqualTo(DEFAULT_BENEVOLE);
		assertThat(testAdherent.getRemarqueBenevolat()).isEqualTo(DEFAULT_REMARQUE_BENEVOLAT);
		assertThat(testAdherent.getAutreRemarque()).isEqualTo(DEFAULT_AUTRE_REMARQUE);
	}

	@Test
	@Transactional
	public void createAdherentWithCoordinates() throws Exception {
		// Given
		final int databaseSizeBeforeCreate = adherentRepository.findAll().size();
		final Coordonnees coordonnees = new Coordonnees();
		coordonnees.setAdresse1("15 Rue de Paris");
		coordonnees.setCodePostal("35000");
		coordonnees.setVille("Rennes");
		adherent.setCoordonnees(coordonnees);

		// When
		restAdherentMockMvc.perform(post("/api/adherents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(adherent)))
		.andExpect(status().isCreated());

		// Then
		final List<Adherent> adherents = adherentRepository.findAll();
		assertThat(adherents).hasSize(databaseSizeBeforeCreate + 1);
		final Adherent testAdherent = adherents.get(adherents.size() - 1);
		assertThat(testAdherent.getCoordonnees()).isNotNull().isEqualToComparingOnlyGivenFields(coordonnees, "adresse1", "codePostal", "ville");
	}

	@Test
	@Transactional
	public void checkPrenomIsRequired() throws Exception {
		// Validate the database is empty
		assertThat(adherentRepository.findAll()).hasSize(0);
		// set the field null
		adherent.setPrenom(null);

		// Create the Adherent, which fails.
		restAdherentMockMvc.perform(post("/api/adherents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(adherent)))
		.andExpect(status().isBadRequest());

		// Validate the database is still empty
		final List<Adherent> adherents = adherentRepository.findAll();
		assertThat(adherents).hasSize(0);
	}

	@Test
	@Transactional
	public void checkNomIsRequired() throws Exception {
		// Validate the database is empty
		assertThat(adherentRepository.findAll()).hasSize(0);
		// set the field null
		adherent.setNom(null);

		// Create the Adherent, which fails.
		restAdherentMockMvc.perform(post("/api/adherents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(adherent)))
		.andExpect(status().isBadRequest());

		// Validate the database is still empty
		final List<Adherent> adherents = adherentRepository.findAll();
		assertThat(adherents).hasSize(0);
	}

	@Test
	@Transactional
	public void getAllAdherents() throws Exception {
		// Initialize the database
		adherentRepository.saveAndFlush(adherent);

		// Get all the adherents
		restAdherentMockMvc.perform(get("/api/adherents")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.[*].id").value(hasItem(adherent.getId().intValue())))
		.andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM.toString())))
		.andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
		.andExpect(jsonPath("$.[*].benevole").value(hasItem(DEFAULT_BENEVOLE.booleanValue())))
		.andExpect(jsonPath("$.[*].remarqueBenevolat").value(hasItem(DEFAULT_REMARQUE_BENEVOLAT.toString())))
		.andExpect(jsonPath("$.[*].autreRemarque").value(hasItem(DEFAULT_AUTRE_REMARQUE.toString())));
	}

	@Test
	@Transactional
	public void getAdherent() throws Exception {
		// Initialize the database
		adherentRepository.saveAndFlush(adherent);

		// Get the adherent
		restAdherentMockMvc.perform(get("/api/adherents/{id}", adherent.getId())).andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.id").value(adherent.getId().intValue()))
		.andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM.toString())).andExpect(jsonPath("$.nom").value(DEFAULT_NOM.toString()))
		.andExpect(jsonPath("$.benevole").value(DEFAULT_BENEVOLE.booleanValue()))
		.andExpect(jsonPath("$.remarqueBenevolat").value(DEFAULT_REMARQUE_BENEVOLAT.toString()))
		.andExpect(jsonPath("$.autreRemarque").value(DEFAULT_AUTRE_REMARQUE.toString()));
	}

	@Test
	@Transactional
	public void getNonExistingAdherent() throws Exception {
		// Get the adherent
		restAdherentMockMvc.perform(get("/api/adherents/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	public void updateAdherent() throws Exception {
		// Initialize the database
		final Adhesion uneAdhesion = new Adhesion();
		uneAdhesion.setTypeAdhesion(TypeAdhesion.Simple);
		uneAdhesion.setDateAdhesion(LocalDate.now());
		adherent.setAdhesions(Sets.newHashSet(uneAdhesion));
		adherentRepository.saveAndFlush(adherent);

		final int databaseSizeBeforeUpdate = adherentRepository.findAll().size();

		// Update the adherent
		adherent.setPrenom(UPDATED_PRENOM);
		adherent.setNom(UPDATED_NOM);
		adherent.setBenevole(UPDATED_BENEVOLE);
		adherent.setRemarqueBenevolat(UPDATED_REMARQUE_BENEVOLAT);
		adherent.setAutreRemarque(UPDATED_AUTRE_REMARQUE);
		restAdherentMockMvc.perform(put("/api/adherents").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(adherent)))
		.andExpect(status().isOk());

		// Validate the Adherent in the database
		final List<Adherent> adherents = adherentRepository.findAll();
		assertThat(adherents).hasSize(databaseSizeBeforeUpdate);
		final Adherent testAdherent = adherents.get(adherents.size() - 1);
		assertThat(testAdherent.getPrenom()).isEqualTo(UPDATED_PRENOM);
		assertThat(testAdherent.getNom()).isEqualTo(UPDATED_NOM);
		assertThat(testAdherent.getBenevole()).isEqualTo(UPDATED_BENEVOLE);
		assertThat(testAdherent.getRemarqueBenevolat()).isEqualTo(UPDATED_REMARQUE_BENEVOLAT);
		assertThat(testAdherent.getAutreRemarque()).isEqualTo(UPDATED_AUTRE_REMARQUE);
	}

	@Test
	@Transactional
	public void deleteAdherent() throws Exception {
		// Initialize the database
		adherentRepository.saveAndFlush(adherent);

		final int databaseSizeBeforeDelete = adherentRepository.findAll().size();

		// Get the adherent
		restAdherentMockMvc.perform(delete("/api/adherents/{id}", adherent.getId()).accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isOk());

		// Validate the database is empty
		final List<Adherent> adherents = adherentRepository.findAll();
		assertThat(adherents).hasSize(databaseSizeBeforeDelete - 1);
	}

	@Test
	@Transactional
	public void searchAdherentIgnoreAccent() throws Exception {
		// Given
		adherent.setPrenom("Gaël");
		adherentRepository.saveAndFlush(adherent);

		final FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		fullTextEntityManager.flushToIndexes();

		// When
		restAdherentMockMvc.perform(get("/api/adherents/search?criteria=gAEl").accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isOk())
		.andExpect(jsonPath("$.[*].prenom").value(hasItem(adherent.getPrenom())));
	}

	@Test
	@Transactional
	public void searchAdherentContaining() throws Exception {
		// Given
		adherent.setNom("FERRÉ");
		adherentRepository.saveAndFlush(adherent);

		final FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		fullTextEntityManager.flushToIndexes();

		// When
		restAdherentMockMvc.perform(get("/api/adherents/search?criteria=eRrE").accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isOk())
		.andExpect(jsonPath("$.[*].nom").value(hasItem(adherent.getNom())));
	}

	@Test
	@Transactional
	public void searchAdherentSortOnName() throws Exception {
		// Given
		adherent.setNom("FERRÉ");
		adherentRepository.saveAndFlush(adherent);

		final Adherent secondAdherent = new Adherent();
		secondAdherent.setPrenom(DEFAULT_PRENOM);
		secondAdherent.setNom("BERRUBÉ");
		adherentRepository.saveAndFlush(secondAdherent);

		final FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		fullTextEntityManager.flushToIndexes();

		// When
		restAdherentMockMvc.perform(get("/api/adherents/search?criteria=eRR&sort=nom").accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isOk())
		.andExpect(jsonPath("$.[*].nom").value(contains("BERRUBÉ", "FERRÉ")));
	}

	@Test
	@Transactional
	public void searchAdherentMultipleTerms() throws Exception {
		// Given
		adherent.setPrenom("Goulven");
		adherent.setNom("Le Breton");
		adherentRepository.saveAndFlush(adherent);

		final FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		fullTextEntityManager.flushToIndexes();

		// When
		restAdherentMockMvc.perform(get("/api/adherents/search?criteria=goulven le b").accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isOk())
		.andExpect(jsonPath("$.[*].prenom").value(hasItem("Goulven")));
	}
}
