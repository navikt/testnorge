package no.nav.pdl.forvalter.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.PdlTestdataConsumer;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.domain.PdlDbVersjon;
import no.nav.pdl.forvalter.domain.PdlKontaktadresse;
import no.nav.pdl.forvalter.utils.PdlTestDataUrls;
import no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_ADRESSEBESKYTTELSE;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_BOSTEDADRESSE;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_DELTBOSTED;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_DOEDSFALL;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_FAMILIERELASJON;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_FOEDSEL;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_FOLKEREGISTER_PERSONSTATUS;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_FORELDREANSVAR;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_FULLMAKT;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_INNFLYTTING;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_KJOENN;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_KONTAKTADRESSE;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_NAVN;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_OPPHOLD;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_OPPHOLDSADRESSE;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_SIVILSTAND;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_STATSBORGERSKAP;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_TELEFONUMMER;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_UTENLANDS_IDENTIFIKASJON_NUMMER;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_UTFLYTTING;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_VERGEMAAL;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlStatus.FEIL;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlStatus.OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlOrdreService {

    private final PdlTestdataConsumer pdlTestdataConsumer;
    private final PersonRepository personRepository;
    private final MapperFacade mapperFacade;

    public List<PdlStatus> sendTilPdl(String ident) {

        var pdlPerson = personRepository.findByIdent(ident)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, format("Ident %s ikke funnet", ident)));

        var status = new ArrayList<PdlStatus>();
        pdlTestdataConsumer.deletePerson(ident);
        pdlTestdataConsumer.createPerson(ident);
        status.addAll(sendPdlArtifact(PDL_NAVN, ident, pdlPerson.getPerson().getNavn()));
        status.addAll(sendPdlArtifact(PDL_KJOENN, ident, pdlPerson.getPerson().getKjoenn()));
        status.addAll(sendPdlArtifact(PDL_FOEDSEL, ident, pdlPerson.getPerson().getFoedsel()));
        status.addAll(sendPdlArtifact(PDL_FOLKEREGISTER_PERSONSTATUS, ident, pdlPerson.getPerson().getFolkeregisterpersonstatus()));
        status.addAll(sendPdlArtifact(PDL_STATSBORGERSKAP, ident, pdlPerson.getPerson().getStatsborgerskap()));
        status.addAll(sendPdlArtifact(PDL_BOSTEDADRESSE, ident, pdlPerson.getPerson().getBostedsadresse()));
        status.addAll(sendPdlArtifact(PDL_OPPHOLDSADRESSE, ident, pdlPerson.getPerson().getOppholdsadresse()));
        status.addAll(sendPdlArtifact(PDL_KONTAKTADRESSE, ident,
                mapperFacade.mapAsList(pdlPerson.getPerson().getKontaktadresse(), PdlKontaktadresse.class)));
        status.addAll(sendPdlArtifact(PDL_ADRESSEBESKYTTELSE, ident, pdlPerson.getPerson().getAdressebeskyttelse()));
        status.addAll(sendPdlArtifact(PDL_INNFLYTTING, ident, pdlPerson.getPerson().getInnflytting()));
        status.addAll(sendPdlArtifact(PDL_UTFLYTTING, ident, pdlPerson.getPerson().getUtflytting()));
        status.addAll(sendPdlArtifact(PDL_DELTBOSTED, ident, pdlPerson.getPerson().getDeltBosted()));
        status.addAll(sendPdlArtifact(PDL_FORELDREANSVAR, ident, pdlPerson.getPerson().getForeldreansvar()));
        status.addAll(sendPdlArtifact(PDL_FAMILIERELASJON, ident, pdlPerson.getPerson().getForelderBarnRelasjon()));
        status.addAll(sendPdlArtifact(PDL_SIVILSTAND, ident, pdlPerson.getPerson().getSivilstand()));
        status.addAll(sendPdlArtifact(PDL_VERGEMAAL, ident, pdlPerson.getPerson().getVergemaal()));
        status.addAll(sendPdlArtifact(PDL_FULLMAKT, ident, pdlPerson.getPerson().getFullmakt()));
        status.addAll(sendPdlArtifact(PDL_TELEFONUMMER, ident, pdlPerson.getPerson().getTelefonnummer()));
        status.addAll(sendPdlArtifact(PDL_OPPHOLD, ident, pdlPerson.getPerson().getOpphold()));
        status.addAll(sendPdlArtifact(PDL_DOEDSFALL, ident, pdlPerson.getPerson().getDoedsfall()));
//        sendPdlArtifact(PDL_BESTILLING_KONTAKTINFORMASJON_FOR_DODESDBO_URL, ident,
//                pdlPerson.getPerson().getKontaktinformasjonForDoedsbo());
        status.addAll(sendPdlArtifact(PDL_UTENLANDS_IDENTIFIKASJON_NUMMER, ident,
                pdlPerson.getPerson().getUtenlandskIdentifikasjonsnummer()));
//        sendPdlArtifact(PDL_BESTILLING_FALSK_IDENTITET_URL, ident, pdlPerson.getPerson().getFalskIdentitet());
        return status;
    }

    private <T> List<PdlStatus> sendPdlArtifact(PdlArtifact type, String ident, List<T> artifact) {

        var status = new ArrayList<Hendelse>();
        if (!artifact.isEmpty()) {
            artifact.stream()
                    .collect(Collectors.toCollection(LinkedList::new))
                    .descendingIterator()
                    .forEachRemaining(element -> {
                                try {
                                    status.add(Hendelse.builder()
                                            .id(((PdlDbVersjon) element).getId())
                                            .status(OK)
                                            .hendelseId(pdlTestdataConsumer.sendArtifactToPdl(type, ident, element)
                                                    .getHendelseId())
                                            .build());
                                } catch (HttpClientErrorException e) {
                                    log.error("Feil ved skriving av PDL-testdata. {}", e.getResponseBodyAsString());
                                    status.add(Hendelse.builder()
                                            .id(((PdlDbVersjon) element).getId())
                                            .status(FEIL)
                                            .error(e.getResponseBodyAsString())
                                            .build());
                                } catch (JsonProcessingException e) {
                                    status.add(Hendelse.builder()
                                            .id(((PdlDbVersjon) element).getId())
                                            .status(FEIL)
                                            .error(e.getMessage())
                                            .build());
                                    log.error("Feil ved skriving av PDL-testdata: {}", e.getMessage(), e);
                                }
                            }
                    );
        }

        return status.isEmpty() ? emptyList() : List.of(PdlStatus.builder()
                .infoElement(type)
                .hendelser(status)
                .build());
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PdlStatus {

        private PdlArtifact infoElement;
        private List<Hendelse> hendelser;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Hendelse {

        private Integer id;
        private PdlTestDataUrls.PdlStatus status;
        private String hendelseId;
        private String error;
    }
}
