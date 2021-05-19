package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.domain.PdlInnflytting;
import no.nav.pdl.forvalter.domain.PdlKontaktadresse;
import no.nav.pdl.forvalter.domain.PdlTilrettelagtKommunikasjon;
import no.nav.pdl.forvalter.domain.PdlUtflytting;
import no.nav.pdl.forvalter.dto.PdlOrdreResponse;
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

    private final DeployServie deployServie;
    private final PersonRepository personRepository;
    private final MapperFacade mapperFacade;

    public List<PdlOrdreResponse.PdlStatus> sendTilPdl(String ident) {

        var pdlPerson = personRepository.findByIdent(ident)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, format("Ident %s ikke funnet", ident)));

        var status = new ArrayList<PdlOrdreResponse.PdlStatus>();
        status.addAll(deployServie.delete(ident));
        status.addAll(deployServie.create(ident));
        status.addAll(deployServie.put(PDL_NAVN, ident, pdlPerson.getPerson().getNavn()));
        status.addAll(deployServie.put(PDL_KJOENN, ident, pdlPerson.getPerson().getKjoenn()));
        status.addAll(deployServie.put(PDL_FOEDSEL, ident, pdlPerson.getPerson().getFoedsel()));
        status.addAll(deployServie.put(PDL_FOLKEREGISTER_PERSONSTATUS, ident, pdlPerson.getPerson().getFolkeregisterpersonstatus()));
        status.addAll(deployServie.put(PDL_STATSBORGERSKAP, ident, pdlPerson.getPerson().getStatsborgerskap()));
        status.addAll(deployServie.put(PDL_KONTAKTADRESSE, ident,
                mapperFacade.mapAsList(pdlPerson.getPerson().getKontaktadresse(), PdlKontaktadresse.class)));
        status.addAll(deployServie.put(PDL_BOSTEDADRESSE, ident, pdlPerson.getPerson().getBostedsadresse()));
        status.addAll(deployServie.put(PDL_OPPHOLDSADRESSE, ident, pdlPerson.getPerson().getOppholdsadresse()));
        status.addAll(deployServie.put(PDL_ADRESSEBESKYTTELSE, ident, pdlPerson.getPerson().getAdressebeskyttelse()));
        status.addAll(deployServie.put(PDL_INNFLYTTING, ident,
                mapperFacade.mapAsList(pdlPerson.getPerson().getInnflytting(), PdlInnflytting.class)));
        status.addAll(deployServie.put(PDL_UTFLYTTING, ident,
                mapperFacade.mapAsList(pdlPerson.getPerson().getUtflytting(), PdlUtflytting.class)));
        status.addAll(deployServie.put(PDL_DELTBOSTED, ident, pdlPerson.getPerson().getDeltBosted()));
        status.addAll(deployServie.put(PDL_FORELDREANSVAR, ident, pdlPerson.getPerson().getForeldreansvar()));
        status.addAll(deployServie.put(PDL_FAMILIERELASJON, ident, pdlPerson.getPerson().getForelderBarnRelasjon()));
        status.addAll(deployServie.put(PDL_SIVILSTAND, ident, pdlPerson.getPerson().getSivilstand()));
        status.addAll(deployServie.put(PDL_VERGEMAAL, ident, pdlPerson.getPerson().getVergemaal()));
        status.addAll(deployServie.put(PDL_FULLMAKT, ident, pdlPerson.getPerson().getFullmakt()));
        status.addAll(deployServie.put(PDL_TELEFONUMMER, ident, pdlPerson.getPerson().getTelefonnummer()));
        status.addAll(deployServie.put(PDL_OPPHOLD, ident, pdlPerson.getPerson().getOpphold()));
        status.addAll(deployServie.put(PDL_DOEDSFALL, ident, pdlPerson.getPerson().getDoedsfall()));
        status.addAll(deployServie.put(PDL_KONTAKTINFORMASJON_FOR_DODESDBO, ident,
                pdlPerson.getPerson().getKontaktinformasjonForDoedsbo()));
        status.addAll(deployServie.put(PDL_UTENLANDS_IDENTIFIKASJON_NUMMER, ident,
                pdlPerson.getPerson().getUtenlandskIdentifikasjonsnummer()));
        status.addAll(deployServie.put(PDL_FALSK_IDENTITET, ident, pdlPerson.getPerson().getFalskIdentitet()));
        status.addAll(deployServie.put(PDL_TILRETTELAGT_KOMMUNIKASJON, ident,
                mapperFacade.mapAsList(pdlPerson.getPerson().getTilrettelagtKommunikasjon(), PdlTilrettelagtKommunikasjon.class)));

        return status;
    }
}
