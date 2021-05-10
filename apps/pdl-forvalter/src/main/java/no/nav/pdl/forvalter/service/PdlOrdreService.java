package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.PdlTestdataConsumer;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.domain.PdlKontaktadresse;
import no.nav.pdl.forvalter.domain.PdlTilrettelagtKommunikasjon;
import no.nav.pdl.forvalter.dto.PdlOrdreResponse;
import no.nav.pdl.forvalter.service.command.PdlDeployCommand;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_ADRESSEBESKYTTELSE;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_BOSTEDADRESSE;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_DELTBOSTED;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_DOEDSFALL;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_FALSK_IDENTITET;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_FAMILIERELASJON;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_FOEDSEL;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_FOLKEREGISTER_PERSONSTATUS;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_FORELDREANSVAR;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_FULLMAKT;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_INNFLYTTING;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_KJOENN;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_KONTAKTADRESSE;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_KONTAKTINFORMASJON_FOR_DODESDBO;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_NAVN;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_OPPHOLD;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_OPPHOLDSADRESSE;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_SIVILSTAND;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_STATSBORGERSKAP;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_TELEFONUMMER;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_TILRETTELAGT_KOMMUNIKASJON;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_UTENLANDS_IDENTIFIKASJON_NUMMER;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_UTFLYTTING;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_VERGEMAAL;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlOrdreService {

    private final PdlTestdataConsumer pdlTestdataConsumer;
    private final PersonRepository personRepository;
    private final MapperFacade mapperFacade;

    public List<PdlOrdreResponse.PdlStatus> sendTilPdl(String ident) {

        var pdlPerson = personRepository.findByIdent(ident)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, format("Ident %s ikke funnet", ident)));

        var status = new ArrayList<PdlOrdreResponse.PdlStatus>();
        pdlTestdataConsumer.deletePerson(ident);
        pdlTestdataConsumer.createPerson(ident);
        status.addAll(new PdlDeployCommand(PDL_NAVN, ident, pdlPerson.getPerson().getNavn(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_KJOENN, ident, pdlPerson.getPerson().getKjoenn(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_FOEDSEL, ident, pdlPerson.getPerson().getFoedsel(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_FOLKEREGISTER_PERSONSTATUS, ident, pdlPerson.getPerson().getFolkeregisterpersonstatus(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_STATSBORGERSKAP, ident, pdlPerson.getPerson().getStatsborgerskap(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_KONTAKTADRESSE, ident,
                mapperFacade.mapAsList(pdlPerson.getPerson().getKontaktadresse(), PdlKontaktadresse.class), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_BOSTEDADRESSE, ident, pdlPerson.getPerson().getBostedsadresse(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_OPPHOLDSADRESSE, ident, pdlPerson.getPerson().getOppholdsadresse(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_ADRESSEBESKYTTELSE, ident, pdlPerson.getPerson().getAdressebeskyttelse(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_INNFLYTTING, ident, pdlPerson.getPerson().getInnflytting(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_UTFLYTTING, ident, pdlPerson.getPerson().getUtflytting(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_DELTBOSTED, ident, pdlPerson.getPerson().getDeltBosted(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_FORELDREANSVAR, ident, pdlPerson.getPerson().getForeldreansvar(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_FAMILIERELASJON, ident, pdlPerson.getPerson().getForelderBarnRelasjon(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_SIVILSTAND, ident, pdlPerson.getPerson().getSivilstand(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_VERGEMAAL, ident, pdlPerson.getPerson().getVergemaal(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_FULLMAKT, ident, pdlPerson.getPerson().getFullmakt(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_TELEFONUMMER, ident, pdlPerson.getPerson().getTelefonnummer(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_OPPHOLD, ident, pdlPerson.getPerson().getOpphold(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_DOEDSFALL, ident, pdlPerson.getPerson().getDoedsfall(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_KONTAKTINFORMASJON_FOR_DODESDBO, ident,
                pdlPerson.getPerson().getKontaktinformasjonForDoedsbo(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_UTENLANDS_IDENTIFIKASJON_NUMMER, ident,
                pdlPerson.getPerson().getUtenlandskIdentifikasjonsnummer(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_FALSK_IDENTITET, ident, pdlPerson.getPerson().getFalskIdentitet(), pdlTestdataConsumer).call());
        status.addAll(new PdlDeployCommand(PDL_TILRETTELAGT_KOMMUNIKASJON, ident,
                mapperFacade.mapAsList(pdlPerson.getPerson().getTilrettelagtKommunikasjon(), PdlTilrettelagtKommunikasjon.class), pdlTestdataConsumer).call());

        return status;
    }
}
