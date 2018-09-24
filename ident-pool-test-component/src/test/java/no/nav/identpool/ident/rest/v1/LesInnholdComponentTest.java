package no.nav.identpool.ident.rest.v1;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import no.nav.identpool.ComponentTestbase;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Rekvireringsstatus;
import no.nav.identpool.ident.repository.IdentEntity;

public class LesInnholdComponentTest extends ComponentTestbase {
    private static final String PERSONIDENTIFIKATOR = "10108000340";
    private static final Identtype IDENTTYPE = Identtype.FNR;
    private static final Rekvireringsstatus REKVIRERINGSSTATUS = Rekvireringsstatus.I_BRUK;
    private static final String FINNES_HOS_SKATT = "0";
    private static final LocalDate FOEDSELSDATO = LocalDate.of(1980, 10, 10);
    private static final String REKVIRERT_AV = "RekvirererMcRekvirererface";

    @Test
    public void skalLeseInnholdIDatabase() throws URISyntaxException {
        identRepository.save(
                IdentEntity.builder()
                        .identtype(IDENTTYPE)
                        .personidentifikator(PERSONIDENTIFIKATOR)
                        .rekvireringsstatus(REKVIRERINGSSTATUS)
                        .finnesHosSkatt(FINNES_HOS_SKATT)
                        .foedselsdato(FOEDSELSDATO)
                        .rekvirertAv(REKVIRERT_AV)
                        .build()
        );
        URI url = new URIBuilder(IDENT_V1_BASEURL).addParameter("personidentifikator", PERSONIDENTIFIKATOR).build();

        ResponseEntity<IdentEntity> identEntityResponseEntity = testRestTemplate.exchange(url, HttpMethod.GET, lagHttpEntity(false), IdentEntity.class);
        IdentEntity ident = identEntityResponseEntity.getBody();

        assertThat(ident, is(notNullValue()));
        assertThat(ident.getPersonidentifikator(), is(PERSONIDENTIFIKATOR));
        assertThat(ident.getIdenttype(), is(IDENTTYPE));
        assertThat(ident.getRekvireringsstatus(), is(REKVIRERINGSSTATUS));
        assertThat(ident.getFinnesHosSkatt(), is(FINNES_HOS_SKATT));
        assertThat(ident.getFoedselsdato(), is(FOEDSELSDATO));
        assertThat(ident.getRekvirertAv(), is(REKVIRERT_AV));
    }
}
