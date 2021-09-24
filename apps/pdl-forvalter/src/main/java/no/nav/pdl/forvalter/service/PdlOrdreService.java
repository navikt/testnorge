package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.database.model.DbAlias;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.domain.Ordre;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
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
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlOrdreService {

    private final DeployService deployService;
    private final PersonRepository personRepository;
    private final MapperFacade mapperFacade;
    private final ExecutorService pdlForkJoinPool;

    public Flux<OrdreResponseDTO> send(OrdreRequestDTO ordre, Boolean isTpsMaster) {

        var dbPersoner = personRepository.findByIdentIn(ordre.getIdenter());

        var hovedpersoner = dbPersoner.stream()
                .map(DbPerson::getIdent)
                .collect(Collectors.toSet());

        return Flux.fromStream(dbPersoner.stream())
                .flatMap(person -> Mono.zip(
                        sendAlleInformasjonselementer(person, isNotTrue(isTpsMaster))
                                .collectList()
                                .map(ordrer -> PersonHendelserDTO.builder()
                                        .ident(person.getIdent())
                                        .ordrer(ordrer)
                                        .build()),
                        Flux.concat(person.getRelasjoner()
                                        .stream()
                                        .map(DbRelasjon::getRelatertPerson)
                                        .filter(relatertPerson -> !hovedpersoner.contains(relatertPerson.getIdent()))
                                        .map(relatertPerson -> sendAlleInformasjonselementer(relatertPerson, true)
                                                .collectList()
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

    private Flux<OrdreResponseDTO.PdlStatusDTO> sendAlleInformasjonselementer(DbPerson person, boolean skalSlettes) {

        return deployService.sendOrders(Stream.of(
                        conditionalDelete(person.getIdent(), skalSlettes),
                        deployService.createOrder(PDL_OPPRETT_PERSON, person.getIdent(), List.of(HistoriskIdent.builder().identer(person.getAlias().stream().map(DbAlias::getTidligereIdent).collect(Collectors.toList())).build())),
                        deployService.createOrder(PDL_NAVN, person.getIdent(), person.getPerson().getNavn()),
                        deployService.createOrder(PDL_KJOENN, person.getIdent(), person.getPerson().getKjoenn()),
                        deployService.createOrder(PDL_FOEDSEL, person.getIdent(), person.getPerson().getFoedsel()),
                        deployService.createOrder(PDL_FOLKEREGISTER_PERSONSTATUS, person.getIdent(), person.getPerson().getFolkeregisterpersonstatus()),
                        deployService.createOrder(PDL_STATSBORGERSKAP, person.getIdent(), person.getPerson().getStatsborgerskap()),
                        deployService.createOrder(PDL_KONTAKTADRESSE, person.getIdent(), mapperFacade.mapAsList(person.getPerson().getKontaktadresse(), PdlKontaktadresse.class)),
                        deployService.createOrder(PDL_BOSTEDADRESSE, person.getIdent(), person.getPerson().getBostedsadresse()),
                        deployService.createOrder(PDL_OPPHOLDSADRESSE, person.getIdent(), person.getPerson().getOppholdsadresse()),
                        deployService.createOrder(PDL_ADRESSEBESKYTTELSE, person.getIdent(), person.getPerson().getAdressebeskyttelse()),
                        deployService.createOrder(PDL_INNFLYTTING, person.getIdent(), mapperFacade.mapAsList(person.getPerson().getInnflytting(), PdlInnflytting.class)),
                        deployService.createOrder(PDL_UTFLYTTING, person.getIdent(), person.getPerson().getUtflytting()),
                        deployService.createOrder(PDL_DELTBOSTED, person.getIdent(), person.getPerson().getDeltBosted()),
                        deployService.createOrder(PDL_FORELDREANSVAR, person.getIdent(), mapperFacade.mapAsList(person.getPerson().getForeldreansvar(), PdlForeldreansvar.class)),
                        deployService.createOrder(PDL_FORELDRE_BARN_RELASJON, person.getIdent(), person.getPerson().getForelderBarnRelasjon()),
                        deployService.createOrder(PDL_SIVILSTAND, person.getIdent(), person.getPerson().getSivilstand()),
                        deployService.createOrder(PDL_VERGEMAAL, person.getIdent(), mapperFacade.mapAsList(person.getPerson().getVergemaal(), PdlVergemaal.class)),
                        deployService.createOrder(PDL_FULLMAKT, person.getIdent(), person.getPerson().getFullmakt()),
                        deployService.createOrder(PDL_TELEFONUMMER, person.getIdent(), person.getPerson().getTelefonnummer()),
                        deployService.createOrder(PDL_OPPHOLD, person.getIdent(), person.getPerson().getOpphold()),
                        deployService.createOrder(PDL_DOEDSFALL, person.getIdent(), person.getPerson().getDoedsfall()),
                        deployService.createOrder(PDL_KONTAKTINFORMASJON_FOR_DODESDBO, person.getIdent(), person.getPerson().getKontaktinformasjonForDoedsbo()),
                        deployService.createOrder(PDL_UTENLANDS_IDENTIFIKASJON_NUMMER, person.getIdent(), person.getPerson().getUtenlandskIdentifikasjonsnummer()),
                        deployService.createOrder(PDL_FALSK_IDENTITET, person.getIdent(), mapperFacade.mapAsList(person.getPerson().getFalskIdentitet(), PdlFalskIdentitet.class)),
                        deployService.createOrder(PDL_TILRETTELAGT_KOMMUNIKASJON, person.getIdent(), mapperFacade.mapAsList(person.getPerson().getTilrettelagtKommunikasjon(), PdlTilrettelagtKommunikasjon.class)),
                        deployService.createOrder(PDL_DOEDFOEDT_BARN, person.getIdent(), person.getPerson().getDoedfoedtBarn())
                ).reduce(new ArrayList(), (acc, next) -> {
                    acc.addAll(next);
                    return acc;
                }));
    }

    private List<Ordre> conditionalDelete(String ident, boolean skalSlettes) {

        return skalSlettes ? deployService.createOrder(PDL_SLETTING, ident, List.of(new PdlDelete())) : Collections.emptyList();
    }
}