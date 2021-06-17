package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.database.model.DbAlias;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.domain.PdlDelete;
import no.nav.pdl.forvalter.domain.PdlFalskIdentitet;
import no.nav.pdl.forvalter.domain.PdlInnflytting;
import no.nav.pdl.forvalter.domain.PdlKontaktadresse;
import no.nav.pdl.forvalter.domain.PdlTilrettelagtKommunikasjon;
import no.nav.pdl.forvalter.domain.PdlUtflytting;
import no.nav.pdl.forvalter.domain.PdlVergemaal;
import no.nav.pdl.forvalter.dto.HistoriskIdent;
import no.nav.pdl.forvalter.dto.PdlOrdreResponse;
import no.nav.pdl.forvalter.dto.PdlOrdreResponse.PersonHendelser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_OPPRETT_PERSON;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_SIVILSTAND;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlArtifact.PDL_SLETTING;
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

    private final DeployService deployService;
    private final PersonRepository personRepository;
    private final MapperFacade mapperFacade;

    public PdlOrdreResponse send(String ident) {

        var dbPerson = personRepository.findByIdent(ident)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, format("Ident %s ikke funnet", ident)));

        return PdlOrdreResponse.builder()
                .relasjoner(dbPerson.getRelasjoner().stream()
                        .map(DbRelasjon::getRelatertPerson)
                        .map(person -> PersonHendelser.builder()
                                .ident(person.getIdent())
                                .ordrer(sendAlleInformasjonselementer(person, true))
                                .build())
                        .collect(Collectors.toList()))
                .hovedperson(PersonHendelser.builder()
                        .ident(ident)
                        .ordrer(sendAlleInformasjonselementer(dbPerson, false))
                        .build())
                .build();
    }

    private List<PdlOrdreResponse.PdlStatus> sendAlleInformasjonselementer(DbPerson person, boolean isRelasjon) {
        var status = new ArrayList<PdlOrdreResponse.PdlStatus>();
        if (isRelasjon) {
            status.addAll(deployService.send(PDL_SLETTING, person.getIdent(), List.of(new PdlDelete())));
        }
        status.addAll(deployService.send(PDL_OPPRETT_PERSON, person.getIdent(), List.of(HistoriskIdent.builder()
                .identer(person.getAlias().stream()
                        .map(DbAlias::getTidligereIdent)
                        .collect(Collectors.toList()))
                .build())));
        status.addAll(deployService.send(PDL_NAVN, person.getIdent(), person.getPerson().getNavn()));
        status.addAll(deployService.send(PDL_KJOENN, person.getIdent(), person.getPerson().getKjoenn()));
        status.addAll(deployService.send(PDL_FOEDSEL, person.getIdent(), person.getPerson().getFoedsel()));
        status.addAll(deployService.send(PDL_FOLKEREGISTER_PERSONSTATUS, person.getIdent(),
                person.getPerson().getFolkeregisterpersonstatus()));
        status.addAll(deployService.send(PDL_STATSBORGERSKAP, person.getIdent(), person.getPerson().getStatsborgerskap()));
        status.addAll(deployService.send(PDL_KONTAKTADRESSE, person.getIdent(),
                mapperFacade.mapAsList(person.getPerson().getKontaktadresse(), PdlKontaktadresse.class)));
        status.addAll(deployService.send(PDL_BOSTEDADRESSE, person.getIdent(), person.getPerson().getBostedsadresse()));
        status.addAll(deployService.send(PDL_OPPHOLDSADRESSE, person.getIdent(), person.getPerson().getOppholdsadresse()));
        status.addAll(deployService.send(PDL_ADRESSEBESKYTTELSE, person.getIdent(), person.getPerson().getAdressebeskyttelse()));
        status.addAll(deployService.send(PDL_INNFLYTTING, person.getIdent(),
                mapperFacade.mapAsList(person.getPerson().getInnflytting(), PdlInnflytting.class)));
        status.addAll(deployService.send(PDL_UTFLYTTING, person.getIdent(),
                mapperFacade.mapAsList(person.getPerson().getUtflytting(), PdlUtflytting.class)));
        status.addAll(deployService.send(PDL_DELTBOSTED, person.getIdent(), person.getPerson().getDeltBosted()));
        status.addAll(deployService.send(PDL_FORELDREANSVAR, person.getIdent(), person.getPerson().getForeldreansvar()));
        status.addAll(deployService.send(PDL_FAMILIERELASJON, person.getIdent(), person.getPerson().getForelderBarnRelasjon()));
        status.addAll(deployService.send(PDL_SIVILSTAND, person.getIdent(), person.getPerson().getSivilstand()));
        status.addAll(deployService.send(PDL_VERGEMAAL, person.getIdent(),
                mapperFacade.mapAsList(person.getPerson().getVergemaal(), PdlVergemaal.class)));
        status.addAll(deployService.send(PDL_FULLMAKT, person.getIdent(), person.getPerson().getFullmakt()));
        status.addAll(deployService.send(PDL_TELEFONUMMER, person.getIdent(), person.getPerson().getTelefonnummer()));
        status.addAll(deployService.send(PDL_OPPHOLD, person.getIdent(), person.getPerson().getOpphold()));
        status.addAll(deployService.send(PDL_DOEDSFALL, person.getIdent(), person.getPerson().getDoedsfall()));
        status.addAll(deployService.send(PDL_KONTAKTINFORMASJON_FOR_DODESDBO, person.getIdent(),
                person.getPerson().getKontaktinformasjonForDoedsbo()));
        status.addAll(deployService.send(PDL_UTENLANDS_IDENTIFIKASJON_NUMMER, person.getIdent(),
                person.getPerson().getUtenlandskIdentifikasjonsnummer()));
        status.addAll(deployService.send(PDL_FALSK_IDENTITET, person.getIdent(),
                mapperFacade.mapAsList(person.getPerson().getFalskIdentitet(), PdlFalskIdentitet.class)));
        status.addAll(deployService.send(PDL_TILRETTELAGT_KOMMUNIKASJON, person.getIdent(),
                mapperFacade.mapAsList(person.getPerson().getTilrettelagtKommunikasjon(), PdlTilrettelagtKommunikasjon.class)));
        return status;
    }
}
