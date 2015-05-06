package fr.lpr.membership.web.rest;

import fr.lpr.membership.Application;
import fr.lpr.membership.domain.Adresse;
import fr.lpr.membership.repository.AdresseRepository;

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
 * Test class for the AdresseResource REST controller.
 *
 * @see AdresseResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class AdresseResourceTest {

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
    private AdresseRepository adresseRepository;

    private MockMvc restAdresseMockMvc;

    private Adresse adresse;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AdresseResource adresseResource = new AdresseResource();
        ReflectionTestUtils.setField(adresseResource, "adresseRepository", adresseRepository);
        this.restAdresseMockMvc = MockMvcBuilders.standaloneSetup(adresseResource).build();
    }

    @Before
    public void initTest() {
        adresse = new Adresse();
        adresse.setAdresse1(DEFAULT_ADRESSE1);
        adresse.setAdresse2(DEFAULT_ADRESSE2);
        adresse.setCodePostal(DEFAULT_CODE_POSTAL);
        adresse.setVille(DEFAULT_VILLE);
        adresse.setEmail(DEFAULT_EMAIL);
        adresse.setTelephone(DEFAULT_TELEPHONE);
    }

    @Test
    @Transactional
    public void createAdresse() throws Exception {
        int databaseSizeBeforeCreate = adresseRepository.findAll().size();

        // Create the Adresse
        restAdresseMockMvc.perform(post("/api/adresses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(adresse)))
                .andExpect(status().isCreated());

        // Validate the Adresse in the database
        List<Adresse> adresses = adresseRepository.findAll();
        assertThat(adresses).hasSize(databaseSizeBeforeCreate + 1);
        Adresse testAdresse = adresses.get(adresses.size() - 1);
        assertThat(testAdresse.getAdresse1()).isEqualTo(DEFAULT_ADRESSE1);
        assertThat(testAdresse.getAdresse2()).isEqualTo(DEFAULT_ADRESSE2);
        assertThat(testAdresse.getCodePostal()).isEqualTo(DEFAULT_CODE_POSTAL);
        assertThat(testAdresse.getVille()).isEqualTo(DEFAULT_VILLE);
        assertThat(testAdresse.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testAdresse.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
    }

    @Test
    @Transactional
    public void getAllAdresses() throws Exception {
        // Initialize the database
        adresseRepository.saveAndFlush(adresse);

        // Get all the adresses
        restAdresseMockMvc.perform(get("/api/adresses"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(adresse.getId().intValue())))
                .andExpect(jsonPath("$.[*].adresse1").value(hasItem(DEFAULT_ADRESSE1.toString())))
                .andExpect(jsonPath("$.[*].adresse2").value(hasItem(DEFAULT_ADRESSE2.toString())))
                .andExpect(jsonPath("$.[*].codePostal").value(hasItem(DEFAULT_CODE_POSTAL.toString())))
                .andExpect(jsonPath("$.[*].ville").value(hasItem(DEFAULT_VILLE.toString())))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
                .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE.toString())));
    }

    @Test
    @Transactional
    public void getAdresse() throws Exception {
        // Initialize the database
        adresseRepository.saveAndFlush(adresse);

        // Get the adresse
        restAdresseMockMvc.perform(get("/api/adresses/{id}", adresse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(adresse.getId().intValue()))
            .andExpect(jsonPath("$.adresse1").value(DEFAULT_ADRESSE1.toString()))
            .andExpect(jsonPath("$.adresse2").value(DEFAULT_ADRESSE2.toString()))
            .andExpect(jsonPath("$.codePostal").value(DEFAULT_CODE_POSTAL.toString()))
            .andExpect(jsonPath("$.ville").value(DEFAULT_VILLE.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAdresse() throws Exception {
        // Get the adresse
        restAdresseMockMvc.perform(get("/api/adresses/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAdresse() throws Exception {
        // Initialize the database
        adresseRepository.saveAndFlush(adresse);

		int databaseSizeBeforeUpdate = adresseRepository.findAll().size();

        // Update the adresse
        adresse.setAdresse1(UPDATED_ADRESSE1);
        adresse.setAdresse2(UPDATED_ADRESSE2);
        adresse.setCodePostal(UPDATED_CODE_POSTAL);
        adresse.setVille(UPDATED_VILLE);
        adresse.setEmail(UPDATED_EMAIL);
        adresse.setTelephone(UPDATED_TELEPHONE);
        restAdresseMockMvc.perform(put("/api/adresses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(adresse)))
                .andExpect(status().isOk());

        // Validate the Adresse in the database
        List<Adresse> adresses = adresseRepository.findAll();
        assertThat(adresses).hasSize(databaseSizeBeforeUpdate);
        Adresse testAdresse = adresses.get(adresses.size() - 1);
        assertThat(testAdresse.getAdresse1()).isEqualTo(UPDATED_ADRESSE1);
        assertThat(testAdresse.getAdresse2()).isEqualTo(UPDATED_ADRESSE2);
        assertThat(testAdresse.getCodePostal()).isEqualTo(UPDATED_CODE_POSTAL);
        assertThat(testAdresse.getVille()).isEqualTo(UPDATED_VILLE);
        assertThat(testAdresse.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testAdresse.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    public void deleteAdresse() throws Exception {
        // Initialize the database
        adresseRepository.saveAndFlush(adresse);

		int databaseSizeBeforeDelete = adresseRepository.findAll().size();

        // Get the adresse
        restAdresseMockMvc.perform(delete("/api/adresses/{id}", adresse.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Adresse> adresses = adresseRepository.findAll();
        assertThat(adresses).hasSize(databaseSizeBeforeDelete - 1);
    }
}
