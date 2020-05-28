package no.nav.dolly.bestilling.pdlforvalter;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_PERSON_IDENT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdressebeskyttelse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlBostedadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlDoedsfall;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFamilierelasjon;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFoedsel;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlInnflytting;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKjoenn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlNavn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpprettPerson;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlStatsborgerskap;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlTelefonnummer;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlUtflytting;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.PdlFalskIdentitet;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.StsOidcService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlForvalterConsumer {

    private static final String PDL_BESTILLING_URL = "/api/v1/bestilling";
    private static final String PDL_BESTILL_KONTAKTINFORMASJON_FOR_DODESDBO_URL = PDL_BESTILLING_URL + "/kontaktinformasjonfordoedsbo";
    private static final String PDL_BESTILLING_UTENLANDS_IDENTIFIKASJON_NUMMER_URL = PDL_BESTILLING_URL + "/utenlandsidentifikasjonsnummer";
    private static final String PDL_BESTILLING_FALSK_IDENTITET_URL = PDL_BESTILLING_URL + "/falskidentitet";
    private static final String PDL_BESTILLING_OPPRETT_PERSON = PDL_BESTILLING_URL + "/opprettperson";
    private static final String PDL_BESTILLING_FOEDSEL_URL = PDL_BESTILLING_URL + "/foedsel";
    private static final String PDL_BESTILLING_FAMILIERELASJON = PDL_BESTILLING_URL + "/familierelasjon";
    private static final String PDL_BESTILLING_DOEDSFALL_URL = PDL_BESTILLING_URL + "/doedsfall";
    private static final String PDL_BESTILLING_ADRESSEBESKYTTELSE_URL = PDL_BESTILLING_URL + "/adressebeskyttelse";
    private static final String PDL_BESTILLING_NAVN_URL = PDL_BESTILLING_URL + "/navn";
    private static final String PDL_BESTILLING_KJOENN_URL = PDL_BESTILLING_URL + "/kjoenn";
    private static final String PDL_BESTILLING_STATSBORGERSKAP_URL = PDL_BESTILLING_URL + "/statsborgerskap";
    private static final String PDL_BESTILLING_TELEFONUMMER_URL = PDL_BESTILLING_URL + "/telefonnummer";
    private static final String PDL_BESTILLING_SIVILSTAND_URL = PDL_BESTILLING_URL + "/sivilstand";
    private static final String PDL_BESTILLING_OPPHOLDSADRESSE_URL = PDL_BESTILLING_URL + "/oppholdsadresse";
    private static final String PDL_BESTILLING_KONTAKTADRESSE_URL = PDL_BESTILLING_URL + "/kontaktadresse";
    private static final String PDL_BESTILLING_BOSTEDADRESSE_URL = PDL_BESTILLING_URL + "/bostedsadresse";
    private static final String PDL_BESTILLING_INNFLYTTING_URL = PDL_BESTILLING_URL + "/innflytting";
    private static final String PDL_BESTILLING_UTFLYTTING_URL = PDL_BESTILLING_URL + "/utflytting";
    private static final String PDL_BESTILLING_SLETTING_URL = "/api/v1/personident";
    private static final String PREPROD_ENV = "q";

    private static final String SEND_ERROR = "Feilet å sende %s: %s";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;
    private final StsOidcService stsOidcService;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Timed(name = "providers", tags = { "operation", "pdl_deletePerson" })
    public ResponseEntity deleteIdent(String ident) {
        return restTemplate.exchange(RequestEntity.delete(
                URI.create(providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_SLETTING_URL))
                .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .build(), JsonNode.class);
    }

    @Timed(name = "providers", tags = { "operation", "pdl_opprettPerson" })
    public ResponseEntity postOpprettPerson(PdlOpprettPerson pdlNavn, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_OPPRETT_PERSON,
                pdlNavn, ident, "opprett person");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_navn" })
    public ResponseEntity postNavn(PdlNavn pdlNavn, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_NAVN_URL,
                pdlNavn, ident, "navn");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_kjoenn" })
    public ResponseEntity postKjoenn(PdlKjoenn pdlNavn, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_KJOENN_URL,
                pdlNavn, ident,  "kjønn");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_kontaktinfoDoedsbo" })
    public ResponseEntity postKontaktinformasjonForDoedsbo(PdlKontaktinformasjonForDoedsbo kontaktinformasjonForDoedsbo, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILL_KONTAKTINFORMASJON_FOR_DODESDBO_URL,
                kontaktinformasjonForDoedsbo, ident);
    }

    @Timed(name = "providers", tags = { "operation", "pdl_utenlandsIdentitet" })
    public ResponseEntity postUtenlandskIdentifikasjonsnummer(PdlUtenlandskIdentifikasjonsnummer utenlandskIdentifikasjonsnummer, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_UTENLANDS_IDENTIFIKASJON_NUMMER_URL,
                utenlandskIdentifikasjonsnummer, ident);
    }

    @Timed(name = "providers", tags = { "operation", "pdl_falskIdentitet" })
    public ResponseEntity postFalskIdentitet(PdlFalskIdentitet falskIdentitet, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_FALSK_IDENTITET_URL, falskIdentitet, ident);
    }

    @Timed(name = "providers", tags = { "operation", "pdl_statsborgerskap" })
    public ResponseEntity postStatsborgerskap(PdlStatsborgerskap pdlStatsborgerskap, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_STATSBORGERSKAP_URL,
                pdlStatsborgerskap, ident, "statsborgerskap");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_doedsfall" })
    public ResponseEntity postDoedsfall(PdlDoedsfall pdlDoedsfall, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_DOEDSFALL_URL,
                pdlDoedsfall, ident, "dødsmelding");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_foedsel" })
    public ResponseEntity postFoedsel(PdlFoedsel pdlFoedsel, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_FOEDSEL_URL,
                pdlFoedsel, ident, "fødselsmelding");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_adressebeskyttelse" })
    public ResponseEntity postAdressebeskyttelse(PdlAdressebeskyttelse pdlAdressebeskyttelse, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_ADRESSEBESKYTTELSE_URL,
                pdlAdressebeskyttelse, ident, "adressebeskyttelse");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_familierelasjon" })
    public ResponseEntity postFamilierelasjon(PdlFamilierelasjon familierelasjonn, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_FAMILIERELASJON,
                familierelasjonn, ident, "familierelasjon");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_telefonnummer" })
    public ResponseEntity postTelefonnummer(PdlTelefonnummer.Entry telefonnummer, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_TELEFONUMMER_URL,
                telefonnummer, ident, "telefonnummer");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_sivilstand" })
    public ResponseEntity postSivilstand(PdlSivilstand sivilstand, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_SIVILSTAND_URL,
                sivilstand, ident, "sivilstand");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_oppholdsadresse" })
    public ResponseEntity postOppholdsadresse(PdlOppholdsadresse oppholdsadresse, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_OPPHOLDSADRESSE_URL,
                oppholdsadresse, ident, "oppholdsadresse");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_kontaktadresse" })
    public ResponseEntity postKontaktadresse(PdlKontaktadresse kontaktadresse, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_KONTAKTADRESSE_URL,
                kontaktadresse, ident, "kontaktadresse");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_kontaktadresse" })
    public ResponseEntity postBostedadresse(PdlBostedadresse bostedadresse, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_BOSTEDADRESSE_URL,
                bostedadresse, ident, "bostedadresse");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_innflytting" })
    public ResponseEntity postInnflytting(PdlInnflytting innflytting, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_INNFLYTTING_URL,
                innflytting, ident, "innflytting");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_utflytting" })
    public ResponseEntity postUtflytting(PdlUtflytting utflytting, String ident) {

        return postRequest(
                providersProps.getPdlForvalter().getUrl() + PDL_BESTILLING_UTFLYTTING_URL,
                utflytting, ident, "utflytting");
    }

    private ResponseEntity postRequest(String url, Object body, String ident, String beskrivelse) {

        try {
            return restTemplate.exchange(RequestEntity.post(URI.create(url))
                    .contentType(APPLICATION_JSON)
                    .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                    .header(HEADER_NAV_PERSON_IDENT, ident)
                    .body(body), JsonNode.class);

        } catch (RuntimeException e) {

            throw new DollyFunctionalException(format(SEND_ERROR, beskrivelse,
                    errorStatusDecoder.decodeRuntimeException(e)), e);
        }
    }

    private ResponseEntity postRequest(String url, Object body, String ident) {

        return restTemplate.exchange(RequestEntity.post(URI.create(url))
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .body(body), JsonNode.class);
    }
}
