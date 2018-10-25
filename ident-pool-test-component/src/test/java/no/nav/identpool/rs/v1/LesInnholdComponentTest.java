package no.nav.identpool.rs.v1;

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
import org.springframework.util.LinkedMultiValueMap;

import no.nav.identpool.ComponentTestbase;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Kjoenn;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.repository.IdentEntity;

public class LesInnholdComponentTest extends ComponentTestbase {
    private static final String PERSONIDENTIFIKATOR = "10108000398";
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
                        .kjoenn(Kjoenn.MANN)
                        .personidentifikator(PERSONIDENTIFIKATOR)
                        .rekvireringsstatus(REKVIRERINGSSTATUS)
                        .finnesHosSkatt(FINNES_HOS_SKATT)
                        .foedselsdato(FOEDSELSDATO)
                        .rekvirertAv(REKVIRERT_AV)
                        .build()
        );
        URI url = new URIBuilder(IDENT_V1_BASEURL).build();

        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("personidentifikator", PERSONIDENTIFIKATOR);

        ResponseEntity<IdentEntity> identEntityResponseEntity = testRestTemplate.exchange(
                url,
                HttpMethod.GET,
                httpEntityBuilder.withHeaders(headers).build(),
                IdentEntity.class);

        IdentEntity ident = identEntityResponseEntity.getBody();

        assertThat(ident, is(notNullValue()));
        assertThat(ident.getPersonidentifikator(), is(PERSONIDENTIFIKATOR));
        assertThat(ident.getIdenttype(), is(IDENTTYPE));
        assertThat(ident.getKjoenn(), is(Kjoenn.MANN));
        assertThat(ident.getRekvireringsstatus(), is(REKVIRERINGSSTATUS));
        assertThat(ident.getFinnesHosSkatt(), is(FINNES_HOS_SKATT));
        assertThat(ident.getFoedselsdato(), is(FOEDSELSDATO));
        assertThat(ident.getRekvirertAv(), is(REKVIRERT_AV));
    }
}
