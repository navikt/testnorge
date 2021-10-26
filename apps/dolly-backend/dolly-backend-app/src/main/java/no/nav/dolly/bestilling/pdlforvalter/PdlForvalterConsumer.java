package no.nav.dolly.bestilling.pdlforvalter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdressebeskyttelse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlBostedadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlDeltBosted;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlDoedfoedtBarn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlDoedsfall;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFoedsel;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFolkeregisterpersonstatus;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlForelderBarnRelasjon;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlForeldreansvar;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFullmakt;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlInnflytting;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKjoenn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlNavn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpphold;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpprettPerson;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlSikkerhetstiltak;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlSivilstand;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlStatsborgerskap;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlTelefonnummer;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlUtflytting;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlVergemaal;
import no.nav.dolly.config.credentials.PdlProxyProperties;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.PdlFalskIdentitet;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_PERSON_IDENT;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Service
public class PdlForvalterConsumer {

    private static final String PDL_BESTILLING_URL = "/api/v1/bestilling";
    private static final String PDL_BESTILL_KONTAKTINFORMASJON_FOR_DODESDBO_URL = PDL_BESTILLING_URL + "/kontaktinformasjonfordoedsbo";
    private static final String PDL_BESTILLING_UTENLANDS_IDENTIFIKASJON_NUMMER_URL = PDL_BESTILLING_URL + "/utenlandsidentifikasjonsnummer";
    private static final String PDL_BESTILLING_FALSK_IDENTITET_URL = PDL_BESTILLING_URL + "/falskidentitet";
    private static final String PDL_BESTILLING_OPPRETT_PERSON = PDL_BESTILLING_URL + "/opprettperson";
    private static final String PDL_BESTILLING_FOEDSEL_URL = PDL_BESTILLING_URL + "/foedsel";
    private static final String PDL_BESTILLING_FORELDER_BARN_RELASJON = PDL_BESTILLING_URL + "/forelderbarnrelasjon";
    private static final String PDL_BESTILLING_DOEDSFALL_URL = PDL_BESTILLING_URL + "/doedsfall";
    private static final String PDL_BESTILLING_DOEDFOEDTBARN_URL = PDL_BESTILLING_URL + "/doedfoedtbarn";
    private static final String PDL_BESTILLING_ADRESSEBESKYTTELSE_URL = PDL_BESTILLING_URL + "/adressebeskyttelse";
    private static final String PDL_BESTILLING_NAVN_URL = PDL_BESTILLING_URL + "/navn";
    private static final String PDL_BESTILLING_KJOENN_URL = PDL_BESTILLING_URL + "/kjoenn";
    private static final String PDL_BESTILLING_STATSBORGERSKAP_URL = PDL_BESTILLING_URL + "/statsborgerskap";
    private static final String PDL_BESTILLING_TELEFONUMMER_URL = PDL_BESTILLING_URL + "/telefonnummer";
    private static final String PDL_BESTILLING_SIVILSTAND_URL = PDL_BESTILLING_URL + "/sivilstand";
    private static final String PDL_BESTILLING_OPPHOLDSADRESSE_URL = PDL_BESTILLING_URL + "/oppholdsadresse";
    private static final String PDL_BESTILLING_KONTAKTADRESSE_URL = PDL_BESTILLING_URL + "/kontaktadresse";
    private static final String PDL_BESTILLING_BOSTEDADRESSE_URL = PDL_BESTILLING_URL + "/bostedsadresse";
    private static final String PDL_BESTILLING_DELTBOSTED_URL = PDL_BESTILLING_URL + "/deltbosted";
    private static final String PDL_BESTILLING_INNFLYTTING_URL = PDL_BESTILLING_URL + "/innflytting";
    private static final String PDL_BESTILLING_UTFLYTTING_URL = PDL_BESTILLING_URL + "/utflytting";
    private static final String PDL_BESTILLING_FORELDREANSVAR_URL = PDL_BESTILLING_URL + "/foreldreansvar";
    private static final String PDL_BESTILLING_OPPHOLD_URL = PDL_BESTILLING_URL + "/opphold";
    private static final String PDL_BESTILLING_VERGEMAAL_URL = PDL_BESTILLING_URL + "/vergemaal";
    private static final String PDL_BESTILLING_FULLMAKT_URL = PDL_BESTILLING_URL + "/fullmakt";
    private static final String PDL_BESTILLING_SIKKERHET_TILTAK_URL = PDL_BESTILLING_URL + "/sikkerhetstiltak";
    private static final String PDL_IDENTHISTORIKK_PARAMS = "?historiskePersonidenter=";
    private static final String PDL_IDENTHISTORIKK_PARAMS_2 = "&historiskePersonidenter=";
    private static final String PDL_BESTILLING_FOLKEREGISTERPERSONSTATUS_URL = PDL_BESTILLING_URL + "/folkeregisterpersonstatus";

    private static final String PDL_BESTILLING_SLETTING_URL = "/api/v1/personident";
    private static final String PDL_FORVALTER_URL = "/pdl-testdata";

    private static final String SEND_ERROR = "Feilet å sende %s: %s";

    private final TokenService tokenService;
    private final NaisServerProperties serviceProperties;
    private final WebClient webClient;
    private final ErrorStatusDecoder errorStatusDecoder;

    public PdlForvalterConsumer(TokenService tokenService, PdlProxyProperties serverProperties, ErrorStatusDecoder errorStatusDecoder, ObjectMapper objectMapper) {

        this.serviceProperties = serverProperties;
        this.tokenService = tokenService;
        this.errorStatusDecoder = errorStatusDecoder;
        webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "pdl_deletePerson" })
    public ResponseEntity<JsonNode> deleteIdent(String ident) {
        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_FORVALTER_URL)
                        .path(PDL_BESTILLING_SLETTING_URL)
                        .build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .retrieve().toEntity(JsonNode.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "pdl_opprettPerson" })
    public ResponseEntity<JsonNode> postOpprettPerson(PdlOpprettPerson opprettPerson, String ident) {

        return postRequest(
                PDL_BESTILLING_OPPRETT_PERSON +
                        (opprettPerson.getHistoriskeIdenter().isEmpty() ? "" :
                                PDL_IDENTHISTORIKK_PARAMS + String.join(PDL_IDENTHISTORIKK_PARAMS_2, opprettPerson.getHistoriskeIdenter())),
                opprettPerson, ident, "opprett person");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_navn" })
    public ResponseEntity<JsonNode> postNavn(PdlNavn pdlNavn, String ident) {

        return postRequest(
                PDL_BESTILLING_NAVN_URL,
                pdlNavn, ident, "navn");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_kjoenn" })
    public ResponseEntity<JsonNode> postKjoenn(PdlKjoenn pdlKjoenn, String ident) {

        return postRequest(
                PDL_BESTILLING_KJOENN_URL,
                pdlKjoenn, ident, "kjønn");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_kontaktinfoDoedsbo" })
    public ResponseEntity<JsonNode> postKontaktinformasjonForDoedsbo(PdlKontaktinformasjonForDoedsbo kontaktinformasjonForDoedsbo, String ident) {

        return postRequest(
                PDL_BESTILL_KONTAKTINFORMASJON_FOR_DODESDBO_URL,
                kontaktinformasjonForDoedsbo, ident);
    }

    @Timed(name = "providers", tags = { "operation", "pdl_utenlandsIdentitet" })
    public ResponseEntity<JsonNode> postUtenlandskIdentifikasjonsnummer(PdlUtenlandskIdentifikasjonsnummer utenlandskIdentifikasjonsnummer, String ident) {

        return postRequest(
                PDL_BESTILLING_UTENLANDS_IDENTIFIKASJON_NUMMER_URL,
                utenlandskIdentifikasjonsnummer, ident);
    }

    @Timed(name = "providers", tags = { "operation", "pdl_falskIdentitet" })
    public ResponseEntity<JsonNode> postFalskIdentitet(PdlFalskIdentitet falskIdentitet, String ident) {

        return postRequest(
                PDL_BESTILLING_FALSK_IDENTITET_URL, falskIdentitet, ident);
    }

    @Timed(name = "providers", tags = { "operation", "pdl_statsborgerskap" })
    public ResponseEntity<JsonNode> postStatsborgerskap(PdlStatsborgerskap pdlStatsborgerskap, String ident) {

        return postRequest(
                PDL_BESTILLING_STATSBORGERSKAP_URL,
                pdlStatsborgerskap, ident, "statsborgerskap");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_doedsfall" })
    public ResponseEntity<JsonNode> postDoedsfall(PdlDoedsfall pdlDoedsfall, String ident) {

        return postRequest(
                PDL_BESTILLING_DOEDSFALL_URL,
                pdlDoedsfall, ident, "dødsmelding");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_doedfoedtBarn" })
    public ResponseEntity<JsonNode> postDoedfoedtBarn(PdlDoedfoedtBarn doedfoedtBarn, String ident) {

        return postRequest(
                PDL_BESTILLING_DOEDFOEDTBARN_URL,
                doedfoedtBarn, ident, "dødfødtBarn");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_foedsel" })
    public ResponseEntity<JsonNode> postFoedsel(PdlFoedsel pdlFoedsel, String ident) {

        return postRequest(
                PDL_BESTILLING_FOEDSEL_URL,
                pdlFoedsel, ident, "fødselsmelding");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_adressebeskyttelse" })
    public ResponseEntity<JsonNode> postAdressebeskyttelse(PdlAdressebeskyttelse pdlAdressebeskyttelse, String ident) {

        return postRequest(
                PDL_BESTILLING_ADRESSEBESKYTTELSE_URL,
                pdlAdressebeskyttelse, ident, "adressebeskyttelse");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_familierelasjon" })
    public ResponseEntity<JsonNode> postForeldreBarnRelasjon(PdlForelderBarnRelasjon familierelasjonn, String ident) {

        return postRequest(
                PDL_BESTILLING_FORELDER_BARN_RELASJON,
                familierelasjonn, ident, "familierelasjon");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_telefonnummer" })
    public ResponseEntity<JsonNode> postTelefonnummer(PdlTelefonnummer.Entry telefonnummer, String ident) {

        return postRequest(
                PDL_BESTILLING_TELEFONUMMER_URL,
                telefonnummer, ident, "telefonnummer");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_sivilstand" })
    public ResponseEntity<JsonNode> postSivilstand(PdlSivilstand sivilstand, String ident) {

        return postRequest(
                PDL_BESTILLING_SIVILSTAND_URL,
                sivilstand, ident, "sivilstand");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_oppholdsadresse" })
    public ResponseEntity<JsonNode> postOppholdsadresse(PdlOppholdsadresse oppholdsadresse, String ident) {

        return postRequest(
                PDL_BESTILLING_OPPHOLDSADRESSE_URL,
                oppholdsadresse, ident, "oppholdsadresse");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_kontaktadresse" })
    public ResponseEntity<JsonNode> postKontaktadresse(PdlKontaktadresse kontaktadresse, String ident) {

        return postRequest(
                PDL_BESTILLING_KONTAKTADRESSE_URL,
                kontaktadresse, ident, "kontaktadresse");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_bostedadresse" })
    public ResponseEntity<JsonNode> postBostedadresse(PdlBostedadresse bostedadresse, String ident) {

        return postRequest(
                PDL_BESTILLING_BOSTEDADRESSE_URL,
                bostedadresse, ident, "bostedadresse");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_delt_bosted" })
    public ResponseEntity<JsonNode> postDeltBosted(PdlDeltBosted deltBosted, String ident) {

        return postRequest(
                PDL_BESTILLING_DELTBOSTED_URL,
                deltBosted, ident, "deltBosted");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_folkeregisterpersonstatus" })
    public ResponseEntity<JsonNode> postFolkeregisterpersonstatus(PdlFolkeregisterpersonstatus folkeregisterpersonstatus, String ident) {

        return postRequest(
                PDL_BESTILLING_FOLKEREGISTERPERSONSTATUS_URL,
                folkeregisterpersonstatus, ident, "folkeregisterpersonstatus");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_foreldreansvar" })
    public ResponseEntity<JsonNode> postForeldreansvar(PdlForeldreansvar foreldreansvar, String ident) {

        return postRequest(
                PDL_BESTILLING_FORELDREANSVAR_URL,
                foreldreansvar, ident, "foreldreansvar");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_innflytting" })
    public ResponseEntity<JsonNode> postInnflytting(PdlInnflytting innflytting, String ident) {

        return postRequest(
                PDL_BESTILLING_INNFLYTTING_URL,
                innflytting, ident, "innflytting");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_utflytting" })
    public ResponseEntity<JsonNode> postUtflytting(PdlUtflytting utflytting, String ident) {

        return postRequest(
                PDL_BESTILLING_UTFLYTTING_URL,
                utflytting, ident, "utflytting");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_opphold" })
    public ResponseEntity<JsonNode> postOpphold(PdlOpphold opphold, String ident) {

        return postRequest(
                PDL_BESTILLING_OPPHOLD_URL,
                opphold, ident, "opphold");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_vergemaal" })
    public ResponseEntity<JsonNode> postVergemaal(PdlVergemaal vergemaal, String ident) {

        return postRequest(
                PDL_BESTILLING_VERGEMAAL_URL,
                vergemaal, ident, "vergemaal");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_fullmakt" })
    public ResponseEntity<JsonNode> postFullmakt(PdlFullmakt fullmakt, String ident) {

        return postRequest(
                PDL_BESTILLING_FULLMAKT_URL,
                fullmakt, ident, "fullmakt");
    }

    @Timed(name = "providers", tags = { "operation", "pdl_sikkerhetstiltak" })
    public ResponseEntity<JsonNode> postSikkerhetstiltak(PdlSikkerhetstiltak sikkerhetstiltak, String ident) {

        return postRequest(
                PDL_BESTILLING_SIKKERHET_TILTAK_URL,
                sikkerhetstiltak, ident, "sikkerhetstiltak");
    }

    private ResponseEntity<JsonNode> postRequest(String url, Object body, String ident, String beskrivelse) {

        try {
            return
                    webClient.post()
                            .uri(uriBuilder -> uriBuilder
                                    .path(PDL_FORVALTER_URL)
                                    .path(url)
                                    .build())
                            .contentType(APPLICATION_JSON)
                            .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                            .header(HEADER_NAV_PERSON_IDENT, ident)
                            .bodyValue(body)
                            .retrieve().toEntity(JsonNode.class)
                            .block();

        } catch (RuntimeException e) {

            throw new DollyFunctionalException(format(SEND_ERROR, beskrivelse,
                    errorStatusDecoder.decodeRuntimeException(e)), e);
        }
    }

    private ResponseEntity<JsonNode> postRequest(String url, Object body, String ident) {

        return
                webClient.post()
                        .uri(uriBuilder -> uriBuilder
                                .path(PDL_FORVALTER_URL)
                                .path(url)
                                .build())
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                        .header(HEADER_NAV_PERSON_IDENT, ident)
                        .bodyValue(body)
                        .retrieve().toEntity(JsonNode.class)
                        .block();
    }

    public Map<String, String> checkAlive() {
        try {
            return Map.of(serviceProperties.getName() + PDL_FORVALTER_URL, serviceProperties.checkIsAlive(webClient, serviceProperties.getAccessToken(tokenService)));
        } catch (SecurityException | WebClientResponseException ex) {
            log.error("{} feilet mot URL: {}", serviceProperties.getName(), serviceProperties.getUrl(), ex);
            return Map.of(serviceProperties.getName(), String.format("%s, URL: %s", ex.getMessage(), serviceProperties.getUrl()));
        }
    }
}
