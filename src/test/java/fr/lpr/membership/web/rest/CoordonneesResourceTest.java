package fr.lpr.membership.web.rest;

import fr.lpr.membership.Application;
import fr.lpr.membership.domain.Coordonnees;
import fr.lpr.membership.repository.CoordonneesRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CoordonneesResource REST controller.
 *
 * @see CoordonneesResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class CoordonneesResourceTest {

    private static final String DEFAULT_ADRESSE1 = "SAMPLE_TEXT";
    private static final String UPDATED_ADRESSE1 = "UPDATED_TEXT";
    private static final String DEFAULT_ADRESSE2 = "SAMPLE_TEXT";
    private static final String UPDATED_ADRESSE2 = "UPDATED_TEXT";
    private static final String DEFAULT_CODE_POSTAL = "SAMPLE_TEXT";
    private static final String UPDATED_CODE_POSTAL = "UPDATED_TEXT";
    private static final String DEFAULT_VILLE = "SAMPLE_TEXT";
    private static final String UPDATED_VILLE = "UPDATED_TEXT";
    private static final String DEFAULT_EMAIL = "SAMPLE_TEXT";
    private static final String UPDATED_EMAIL = "UPDATED_TEXT";
    private static final String DEFAULT_TELEPHONE = "SAMPLE_TEXT";
    private static final String UPDATED_TELEPHONE = "UPDATED_TEXT";

    @Inject
    private CoordonneesRepository coordonneesRepository;

    private MockMvc restCoordonneesMockMvc;

    private Coordonnees coordonnees;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CoordonneesResource coordonneesResource = new CoordonneesResource();
        ReflectionTestUtils.setField(coordonneesResource, "coordonneesRepository", coordonneesRepository);
        this.restCoordonneesMockMvc = MockMvcBuilders.standaloneSetup(coordonneesResource).build();
    }

    @Before
    public void initTest() {
        coordonnees = new Coordonnees();
        coordonnees.setAdresse1(DEFAULT_ADRESSE1);
        coordonnees.setAdresse2(DEFAULT_ADRESSE2);
        coordonnees.setCodePostal(DEFAULT_CODE_POSTAL);
        coordonnees.setVille(DEFAULT_VILLE);
        coordonnees.setEmail(DEFAULT_EMAIL);
        coordonnees.setTelephone(DEFAULT_TELEPHONE);
    }

    @Test
    @Transactional
    public void createCoordonnees() throws Exception {
        int databaseSizeBeforeCreate = coordonneesRepository.findAll().size();

        // Create the Coordonnees
        restCoordonneesMockMvc.perform(post("/api/coordonneess")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(coordonnees)))
                .andExpect(status().isCreated());

        // Validate the Coordonnees in the database
        List<Coordonnees> coordonneess = coordonneesRepository.findAll();
        assertThat(coordonneess).hasSize(databaseSizeBeforeCreate + 1);
        Coordonnees testCoordonnees = coordonneess.get(coordonneess.size() - 1);
        assertThat(testCoordonnees.getAdresse1()).isEqualTo(DEFAULT_ADRESSE1);
        assertThat(testCoordonnees.getAdresse2()).isEqualTo(DEFAULT_ADRESSE2);
        assertThat(testCoordonnees.getCodePostal()).isEqualTo(DEFAULT_CODE_POSTAL);
        assertThat(testCoordonnees.getVille()).isEqualTo(DEFAULT_VILLE);
        assertThat(testCoordonnees.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testCoordonnees.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
    }

    @Test
    @Transactional
    public void getAllCoordonneess() throws Exception {
        // Initialize the database
        coordonneesRepository.saveAndFlush(coordonnees);

        // Get all the coordonneess
        restCoordonneesMockMvc.perform(get("/api/coordonneess"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(coordonnees.getId().intValue())))
                .andExpect(jsonPath("$.[*].adresse1").value(hasItem(DEFAULT_ADRESSE1.toString())))
                .andExpect(jsonPath("$.[*].adresse2").value(hasItem(DEFAULT_ADRESSE2.toString())))
                .andExpect(jsonPath("$.[*].codePostal").value(hasItem(DEFAULT_CODE_POSTAL.toString())))
                .andExpect(jsonPath("$.[*].ville").value(hasItem(DEFAULT_VILLE.toString())))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
                .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE.toString())));
    }

    @Test
    @Transactional
    public void getCoordonnees() throws Exception {
        // Initialize the database
        coordonneesRepository.saveAndFlush(coordonnees);

        // Get the coordonnees
        restCoordonneesMockMvc.perform(get("/api/coordonneess/{id}", coordonnees.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(coordonnees.getId().intValue()))
            .andExpect(jsonPath("$.adresse1").value(DEFAULT_ADRESSE1.toString()))
            .andExpect(jsonPath("$.adresse2").value(DEFAULT_ADRESSE2.toString()))
            .andExpect(jsonPath("$.codePostal").value(DEFAULT_CODE_POSTAL.toString()))
            .andExpect(jsonPath("$.ville").value(DEFAULT_VILLE.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCoordonnees() throws Exception {
        // Get the coordonnees
        restCoordonneesMockMvc.perform(get("/api/coordonneess/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCoordonnees() throws Exception {
        // Initialize the database
        coordonneesRepository.saveAndFlush(coordonnees);

		int databaseSizeBeforeUpdate = coordonneesRepository.findAll().size();

        // Update the coordonnees
        coordonnees.setAdresse1(UPDATED_ADRESSE1);
        coordonnees.setAdresse2(UPDATED_ADRESSE2);
        coordonnees.setCodePostal(UPDATED_CODE_POSTAL);
        coordonnees.setVille(UPDATED_VILLE);
        coordonnees.setEmail(UPDATED_EMAIL);
        coordonnees.setTelephone(UPDATED_TELEPHONE);
        restCoordonneesMockMvc.perform(put("/api/coordonneess")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(coordonnees)))
                .andExpect(status().isOk());

        // Validate the Coordonnees in the database
        List<Coordonnees> coordonneess = coordonneesRepository.findAll();
        assertThat(coordonneess).hasSize(databaseSizeBeforeUpdate);
        Coordonnees testCoordonnees = coordonneess.get(coordonneess.size() - 1);
        assertThat(testCoordonnees.getAdresse1()).isEqualTo(UPDATED_ADRESSE1);
        assertThat(testCoordonnees.getAdresse2()).isEqualTo(UPDATED_ADRESSE2);
        assertThat(testCoordonnees.getCodePostal()).isEqualTo(UPDATED_CODE_POSTAL);
        assertThat(testCoordonnees.getVille()).isEqualTo(UPDATED_VILLE);
        assertThat(testCoordonnees.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testCoordonnees.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    public void deleteCoordonnees() throws Exception {
        // Initialize the database
        coordonneesRepository.saveAndFlush(coordonnees);

		int databaseSizeBeforeDelete = coordonneesRepository.findAll().size();

        // Get the coordonnees
        restCoordonneesMockMvc.perform(delete("/api/coordonneess/{id}", coordonnees.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Coordonnees> coordonneess = coordonneesRepository.findAll();
        assertThat(coordonneess).hasSize(databaseSizeBeforeDelete - 1);
    }
}
