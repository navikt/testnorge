package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.database.model.DbAlias;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.dto.HistoriskIdent;
import no.nav.pdl.forvalter.dto.PdlDelete;
import no.nav.pdl.forvalter.dto.PdlFalskIdentitet;
import no.nav.pdl.forvalter.dto.PdlForeldreansvar;
import no.nav.pdl.forvalter.dto.PdlInnflytting;
import no.nav.pdl.forvalter.dto.PdlKontaktadresse;
import no.nav.pdl.forvalter.dto.PdlTilrettelagtKommunikasjon;
import no.nav.pdl.forvalter.dto.PdlVergemaal;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO.PersonHendelserDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_ADRESSEBESKYTTELSE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_BOSTEDADRESSE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_DELTBOSTED;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_DOEDFOEDT_BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_DOEDSFALL;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_FALSK_IDENTITET;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_FOEDSEL;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_FOLKEREGISTER_PERSONSTATUS;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_FORELDREANSVAR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_FORELDRE_BARN_RELASJON;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_FULLMAKT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_INNFLYTTING;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_KJOENN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_KONTAKTADRESSE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_KONTAKTINFORMASJON_FOR_DODESDBO;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_NAVN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_OPPHOLD;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_OPPHOLDSADRESSE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_OPPRETT_PERSON;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_SIVILSTAND;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_SLETTING;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_STATSBORGERSKAP;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_TELEFONUMMER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_TILRETTELAGT_KOMMUNIKASJON;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_UTENLANDS_IDENTIFIKASJON_NUMMER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_UTFLYTTING;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_VERGEMAAL;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlOrdreService {

    private final DeployService deployService;
    private final PersonRepository personRepository;
    private final MapperFacade mapperFacade;

    public Flux<OrdreResponseDTO> send(OrdreRequestDTO ordre) {

        var dbPersoner = personRepository.findByIdentIn(ordre.getIdenter());

        var hovedpersoner = dbPersoner.stream()
                .map(DbPerson::getIdent)
                .collect(Collectors.toSet());

        return Flux.fromStream(dbPersoner.stream())
                .flatMap(person -> Mono.zip(
                        sendAlleInformasjonselementer(person, false)
                                .map(ordrer -> PersonHendelserDTO.builder()
                                        .ident(person.getIdent())
                                        .ordrer(ordrer)
                                        .build()),
                        Flux.concat(person.getRelasjoner()
                                        .parallelStream()
                                        .map(DbRelasjon::getRelatertPerson)
                                        .filter(relatertPerson -> !hovedpersoner.contains(relatertPerson.getIdent()))
                                        .map(relatertPerson -> sendAlleInformasjonselementer(relatertPerson, true)
                                                .map(ordrer -> PersonHendelserDTO.builder()
                                                        .ident(relatertPerson.getIdent())
                                                        .ordrer(ordrer)
                                                        .build()))
                                        .collect(Collectors.toList()))
                                .collectList(),
                        (hovedPerson, relasjoner) -> OrdreResponseDTO.builder()
                                .hovedperson(hovedPerson)
                                .relasjoner(relasjoner)
                                .build()));
    }

    private Mono<List<OrdreResponseDTO.PdlStatusDTO>> sendAlleInformasjonselementer(DbPerson person, boolean isRelasjon) {

        return Stream.of(
                        conditionalDelete(person.getIdent(), isRelasjon),
                        deployService.send(PDL_OPPRETT_PERSON, person.getIdent(), List.of(HistoriskIdent.builder()
                                .identer(person.getAlias().stream()
                                        .map(DbAlias::getTidligereIdent)
                                        .collect(Collectors.toList()))
                                .build())),
                        deployService.send(PDL_NAVN, person.getIdent(), person.getPerson().getNavn()),
                        deployService.send(PDL_KJOENN, person.getIdent(), person.getPerson().getKjoenn()),
                        deployService.send(PDL_FOEDSEL, person.getIdent(), person.getPerson().getFoedsel()),
                        deployService.send(PDL_FOLKEREGISTER_PERSONSTATUS, person.getIdent(),
                                person.getPerson().getFolkeregisterpersonstatus()),
                        deployService.send(PDL_STATSBORGERSKAP, person.getIdent(), person.getPerson().getStatsborgerskap()),
                        deployService.send(PDL_KONTAKTADRESSE, person.getIdent(),
                                mapperFacade.mapAsList(person.getPerson().getKontaktadresse(), PdlKontaktadresse.class)),
                        deployService.send(PDL_BOSTEDADRESSE, person.getIdent(), person.getPerson().getBostedsadresse()),
                        deployService.send(PDL_OPPHOLDSADRESSE, person.getIdent(), person.getPerson().getOppholdsadresse()),
                        deployService.send(PDL_ADRESSEBESKYTTELSE, person.getIdent(), person.getPerson().getAdressebeskyttelse()),
                        deployService.send(PDL_INNFLYTTING, person.getIdent(),
                                mapperFacade.mapAsList(person.getPerson().getInnflytting(), PdlInnflytting.class)),
                        deployService.send(PDL_UTFLYTTING, person.getIdent(), person.getPerson().getUtflytting()),
                        deployService.send(PDL_DELTBOSTED, person.getIdent(), person.getPerson().getDeltBosted()),
                        deployService.send(PDL_FORELDREANSVAR, person.getIdent(),
                                mapperFacade.mapAsList(person.getPerson().getForeldreansvar(), PdlForeldreansvar.class)),
                        deployService.send(PDL_FORELDRE_BARN_RELASJON, person.getIdent(), person.getPerson().getForelderBarnRelasjon()),
                        deployService.send(PDL_SIVILSTAND, person.getIdent(), person.getPerson().getSivilstand()),
                        deployService.send(PDL_VERGEMAAL, person.getIdent(),
                                mapperFacade.mapAsList(person.getPerson().getVergemaal(), PdlVergemaal.class)),
                        deployService.send(PDL_FULLMAKT, person.getIdent(), person.getPerson().getFullmakt()),
                        deployService.send(PDL_TELEFONUMMER, person.getIdent(), person.getPerson().getTelefonnummer()),
                        deployService.send(PDL_OPPHOLD, person.getIdent(), person.getPerson().getOpphold()),
                        deployService.send(PDL_DOEDSFALL, person.getIdent(), person.getPerson().getDoedsfall()),
                        deployService.send(PDL_KONTAKTINFORMASJON_FOR_DODESDBO, person.getIdent(),
                                person.getPerson().getKontaktinformasjonForDoedsbo()),
                        deployService.send(PDL_UTENLANDS_IDENTIFIKASJON_NUMMER, person.getIdent(),
                                person.getPerson().getUtenlandskIdentifikasjonsnummer()),
                        deployService.send(PDL_FALSK_IDENTITET, person.getIdent(),
                                mapperFacade.mapAsList(person.getPerson().getFalskIdentitet(), PdlFalskIdentitet.class)),
                        deployService.send(PDL_TILRETTELAGT_KOMMUNIKASJON, person.getIdent(),
                                mapperFacade.mapAsList(person.getPerson().getTilrettelagtKommunikasjon(), PdlTilrettelagtKommunikasjon.class)),
                        deployService.send(PDL_DOEDFOEDT_BARN, person.getIdent(), person.getPerson().getDoedfoedtBarn())
                )
                .reduce(Flux.empty(), Flux::concat)
                .collectList();
    }

    private Flux<OrdreResponseDTO.PdlStatusDTO> conditionalDelete(String ident, boolean isRelasjon) {

        return isRelasjon ? deployService.send(PDL_SLETTING, ident, List.of(new PdlDelete())) : Flux.empty();
    }
}