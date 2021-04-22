package no.nav.pdl.forvalter.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.consumer.PdlTestdataConsumer;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_ADRESSEBESKYTTELSE_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_BOSTEDADRESSE_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_DELTBOSTED_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_DOEDSFALL_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_FALSK_IDENTITET_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_FAMILIERELASJON;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_FOEDSEL_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_FOLKEREGISTERPERSONSTATUS_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_FORELDREANSVAR_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_FULLMAKT_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_INNFLYTTING_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_KJOENN_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_KONTAKTADRESSE_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_KONTAKTINFORMASJON_FOR_DODESDBO_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_NAVN_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_OPPHOLDSADRESSE_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_OPPHOLD_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_OPPRETT_PERSON;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_SIVILSTAND_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_STATSBORGERSKAP_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_TELEFONUMMER_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_UTENLANDS_IDENTIFIKASJON_NUMMER_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_UTFLYTTING_URL;
import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_VERGEMAAL_URL;

@Service
@RequiredArgsConstructor
public class PdlOrdreService {

    private final PdlTestdataConsumer pdlTestdataConsumer;
    private final PersonRepository personRepository;

    public JsonNode sendTilPdl(String ident) {

        var pdlPerson = personRepository.findByIdent(ident)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, format("Ident %s ikke funnet", ident)));

        pdlTestdataConsumer.deletePerson(ident);
        sendPdlArtifact(PDL_BESTILLING_OPPRETT_PERSON + "?kilde=Dolly", ident,null);
        sendPdlArtifact(PDL_BESTILLING_NAVN_URL, ident, pdlPerson.getPerson().getNavn());
        sendPdlArtifact(PDL_BESTILLING_KJOENN_URL, ident, pdlPerson.getPerson().getKjoenn());
        sendPdlArtifact(PDL_BESTILLING_FOEDSEL_URL, ident, pdlPerson.getPerson().getFoedsel());
        sendPdlArtifact(PDL_BESTILLING_FOLKEREGISTERPERSONSTATUS_URL, ident,
                pdlPerson.getPerson().getFolkeregisterpersonstatus());
        sendPdlArtifact(PDL_BESTILLING_STATSBORGERSKAP_URL, ident, pdlPerson.getPerson().getStatsborgerskap());
        sendPdlArtifact(PDL_BESTILLING_BOSTEDADRESSE_URL, ident, pdlPerson.getPerson().getBostedsadresse());
        sendPdlArtifact(PDL_BESTILLING_OPPHOLDSADRESSE_URL, ident, pdlPerson.getPerson().getOppholdsadresse());
        sendPdlArtifact(PDL_BESTILLING_KONTAKTADRESSE_URL, ident, pdlPerson.getPerson().getKontaktadresse());
        sendPdlArtifact(PDL_BESTILLING_ADRESSEBESKYTTELSE_URL, ident, pdlPerson.getPerson().getAdressebeskyttelse());
        sendPdlArtifact(PDL_BESTILLING_INNFLYTTING_URL, ident, pdlPerson.getPerson().getInnflytting());
        sendPdlArtifact(PDL_BESTILLING_UTFLYTTING_URL, ident, pdlPerson.getPerson().getUtflytting());
        sendPdlArtifact(PDL_BESTILLING_DELTBOSTED_URL, ident, pdlPerson.getPerson().getDeltBosted());
        sendPdlArtifact(PDL_BESTILLING_FORELDREANSVAR_URL, ident, pdlPerson.getPerson().getForeldreansvar());
        sendPdlArtifact(PDL_BESTILLING_FAMILIERELASJON, ident, pdlPerson.getPerson().getForelderBarnRelasjon());
        sendPdlArtifact(PDL_BESTILLING_SIVILSTAND_URL, ident, pdlPerson.getPerson().getSivilstand());
        sendPdlArtifact(PDL_BESTILLING_VERGEMAAL_URL, ident, pdlPerson.getPerson().getVergemaal());
        sendPdlArtifact(PDL_BESTILLING_FULLMAKT_URL, ident, pdlPerson.getPerson().getFullmakt());
        sendPdlArtifact(PDL_BESTILLING_TELEFONUMMER_URL, ident, pdlPerson.getPerson().getTelefonnummer());
        sendPdlArtifact(PDL_BESTILLING_OPPHOLD_URL, ident, pdlPerson.getPerson().getOpphold());
        sendPdlArtifact(PDL_BESTILLING_DOEDSFALL_URL, ident, pdlPerson.getPerson().getDoedsfall());
        sendPdlArtifact(PDL_BESTILLING_KONTAKTINFORMASJON_FOR_DODESDBO_URL, ident,
                pdlPerson.getPerson().getKontaktinformasjonForDoedsbo());
        sendPdlArtifact(PDL_BESTILLING_UTENLANDS_IDENTIFIKASJON_NUMMER_URL, ident,
                pdlPerson.getPerson().getUtenlandskIdentifikasjonsnummer());
        sendPdlArtifact(PDL_BESTILLING_FALSK_IDENTITET_URL, ident, pdlPerson.getPerson().getFalskIdentitet());
        return null;
    }

    private <T> void sendPdlArtifact(String url, String ident, List<T> artifact) {

        if (!artifact.isEmpty()) {
            artifact.stream()
                    .collect(Collectors.toCollection(LinkedList::new))
                    .descendingIterator()
                    .forEachRemaining(element -> {
                                try {
                                    pdlTestdataConsumer.sendTilPdl(url, ident, element);
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                            }
                    );
        }
    }
}
