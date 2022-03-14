package no.nav.identpool.providers.v1;

import no.nav.identpool.ComponentTestbase;
import no.nav.identpool.domain.Ident;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Kjoenn;
import no.nav.identpool.domain.Rekvireringsstatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class LesInnholdComponentTest extends ComponentTestbase {

    private static final String PERSONIDENTIFIKATOR = "10108000398";
    private static final Identtype IDENTTYPE = Identtype.FNR;
    private static final Rekvireringsstatus REKVIRERINGSSTATUS = Rekvireringsstatus.I_BRUK;
    private static final boolean FINNES_HOS_SKATT = false;
    private static final LocalDate FOEDSELSDATO = LocalDate.of(1980, 10, 10);
    private static final String REKVIRERT_AV = "RekvirererMcRekvirererface";

    @BeforeEach
    void populerDatabaseMedTestidenter() {
        Ident ident = createIdentEntity(Identtype.FNR, PERSONIDENTIFIKATOR, REKVIRERINGSSTATUS, 10);
        ident.setRekvirertAv(REKVIRERT_AV);
        identRepository.save(ident);
    }

    @Test
    void skalLeseInnholdIDatabase() throws Exception {

        var resultat = mockMvc.perform(MockMvcRequestBuilders.get(IDENT_V1_BASEURL)
                        .header("personidentifikator", PERSONIDENTIFIKATOR))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Ident ident = objectMapper.readValue(resultat.getResponse().getContentAsString(), Ident.class);

        assertThat(ident, is(notNullValue()));
        assertThat(ident.getPersonidentifikator(), is(PERSONIDENTIFIKATOR));
        assertThat(ident.getIdenttype(), is(IDENTTYPE));
        assertThat(ident.getKjoenn(), is(Kjoenn.MANN));
        assertThat(ident.getRekvireringsstatus(), is(REKVIRERINGSSTATUS));
        assertThat(ident.isFinnesHosSkatt(), is(FINNES_HOS_SKATT));
        assertThat(ident.getFoedselsdato(), is(FOEDSELSDATO));
        assertThat(ident.getRekvirertAv(), is(REKVIRERT_AV));
    }
}
