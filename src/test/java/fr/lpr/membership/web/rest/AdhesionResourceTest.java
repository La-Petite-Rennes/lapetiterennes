package fr.lpr.membership.web.rest;

import fr.lpr.membership.Application;
import fr.lpr.membership.domain.Adhesion;
import fr.lpr.membership.repository.AdhesionRepository;

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
import org.joda.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AdhesionResource REST controller.
 *
 * @see AdhesionResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class AdhesionResourceTest {

    private static final String DEFAULT_TYPE_ADHESION = "SAMPLE_TEXT";
    private static final String UPDATED_TYPE_ADHESION = "UPDATED_TEXT";

    private static final LocalDate DEFAULT_DATE_ADHESION = new LocalDate(0L);
    private static final LocalDate UPDATED_DATE_ADHESION = new LocalDate();

    @Inject
    private AdhesionRepository adhesionRepository;

    private MockMvc restAdhesionMockMvc;

    private Adhesion adhesion;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AdhesionResource adhesionResource = new AdhesionResource();
        ReflectionTestUtils.setField(adhesionResource, "adhesionRepository", adhesionRepository);
        this.restAdhesionMockMvc = MockMvcBuilders.standaloneSetup(adhesionResource).build();
    }

    @Before
    public void initTest() {
        adhesion = new Adhesion();
        adhesion.setTypeAdhesion(DEFAULT_TYPE_ADHESION);
        adhesion.setDateAdhesion(DEFAULT_DATE_ADHESION);
    }

    @Test
    @Transactional
    public void createAdhesion() throws Exception {
        int databaseSizeBeforeCreate = adhesionRepository.findAll().size();

        // Create the Adhesion
        restAdhesionMockMvc.perform(post("/api/adhesions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(adhesion)))
                .andExpect(status().isCreated());

        // Validate the Adhesion in the database
        List<Adhesion> adhesions = adhesionRepository.findAll();
        assertThat(adhesions).hasSize(databaseSizeBeforeCreate + 1);
        Adhesion testAdhesion = adhesions.get(adhesions.size() - 1);
        assertThat(testAdhesion.getTypeAdhesion()).isEqualTo(DEFAULT_TYPE_ADHESION);
        assertThat(testAdhesion.getDateAdhesion()).isEqualTo(DEFAULT_DATE_ADHESION);
    }

    @Test
    @Transactional
    public void checkTypeAdhesionIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(adhesionRepository.findAll()).hasSize(0);
        // set the field null
        adhesion.setTypeAdhesion(null);

        // Create the Adhesion, which fails.
        restAdhesionMockMvc.perform(post("/api/adhesions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(adhesion)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Adhesion> adhesions = adhesionRepository.findAll();
        assertThat(adhesions).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllAdhesions() throws Exception {
        // Initialize the database
        adhesionRepository.saveAndFlush(adhesion);

        // Get all the adhesions
        restAdhesionMockMvc.perform(get("/api/adhesions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(adhesion.getId().intValue())))
                .andExpect(jsonPath("$.[*].typeAdhesion").value(hasItem(DEFAULT_TYPE_ADHESION.toString())))
                .andExpect(jsonPath("$.[*].dateAdhesion").value(hasItem(DEFAULT_DATE_ADHESION.toString())));
    }

    @Test
    @Transactional
    public void getAdhesion() throws Exception {
        // Initialize the database
        adhesionRepository.saveAndFlush(adhesion);

        // Get the adhesion
        restAdhesionMockMvc.perform(get("/api/adhesions/{id}", adhesion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(adhesion.getId().intValue()))
            .andExpect(jsonPath("$.typeAdhesion").value(DEFAULT_TYPE_ADHESION.toString()))
            .andExpect(jsonPath("$.dateAdhesion").value(DEFAULT_DATE_ADHESION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAdhesion() throws Exception {
        // Get the adhesion
        restAdhesionMockMvc.perform(get("/api/adhesions/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAdhesion() throws Exception {
        // Initialize the database
        adhesionRepository.saveAndFlush(adhesion);

		int databaseSizeBeforeUpdate = adhesionRepository.findAll().size();

        // Update the adhesion
        adhesion.setTypeAdhesion(UPDATED_TYPE_ADHESION);
        adhesion.setDateAdhesion(UPDATED_DATE_ADHESION);
        restAdhesionMockMvc.perform(put("/api/adhesions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(adhesion)))
                .andExpect(status().isOk());

        // Validate the Adhesion in the database
        List<Adhesion> adhesions = adhesionRepository.findAll();
        assertThat(adhesions).hasSize(databaseSizeBeforeUpdate);
        Adhesion testAdhesion = adhesions.get(adhesions.size() - 1);
        assertThat(testAdhesion.getTypeAdhesion()).isEqualTo(UPDATED_TYPE_ADHESION);
        assertThat(testAdhesion.getDateAdhesion()).isEqualTo(UPDATED_DATE_ADHESION);
    }

    @Test
    @Transactional
    public void deleteAdhesion() throws Exception {
        // Initialize the database
        adhesionRepository.saveAndFlush(adhesion);

		int databaseSizeBeforeDelete = adhesionRepository.findAll().size();

        // Get the adhesion
        restAdhesionMockMvc.perform(delete("/api/adhesions/{id}", adhesion.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Adhesion> adhesions = adhesionRepository.findAll();
        assertThat(adhesions).hasSize(databaseSizeBeforeDelete - 1);
    }
}
