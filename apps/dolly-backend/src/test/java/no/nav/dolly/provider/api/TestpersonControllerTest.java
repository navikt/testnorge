package no.nav.dolly.provider.api;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.service.PersonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TestpersonControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonService personService;

    @Autowired
    private TestgruppeRepository testgruppeRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Test
    @Transactional
    @DisplayName("Sletter Testident fra Testgruppe")
    void deleteExisting()
            throws Exception {

        var testgruppe = super.createTestgruppe("Testgruppe", super.createBruker());

        var testident1 = super.createTestident("Testident 1", testgruppe);
        var testident2 = super.createTestident("Testident 2", testgruppe);
        var testident3 = super.createTestident("Testident 3", testgruppe);


        testgruppe.getTestidenter().add(testident1);
        testgruppe.getTestidenter().add(testident2);
        testgruppe.getTestidenter().add(testident3);

        testgruppeRepository.save(testgruppe);

        entityManager.flush();

        mockMvc
                .perform(delete("/api/v1/ident/{ident}", testident2.getIdent()))
                .andExpect(status().isOk());

        testgruppe = super
                .findTestgruppeById(testgruppe.getId())
                .orElseThrow(IllegalStateException::new);
        assertThat(testgruppe.getTestidenter())
                .isNotNull()
                .contains(testident1, testident3);

        verify(personService).recyclePersoner(anyList());

    }

    @Test
    @DisplayName("Kan ikke slette ikke-eksisterende Testident")
    void cannotDeleteNonExisting()
            throws Exception {

        var id = new Random().nextLong(100, Long.MAX_VALUE);
        mockMvc
                .perform(delete("/api/v1/ident/{ident}", id))
                .andExpect(status().isNotFound());

    }

}
