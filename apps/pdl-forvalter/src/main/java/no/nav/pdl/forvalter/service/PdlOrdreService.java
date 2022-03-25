package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.database.model.DbAlias;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.AliasRepository;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.dto.HistoriskIdent;
import no.nav.pdl.forvalter.dto.Ordre;
import no.nav.pdl.forvalter.dto.PdlDelete;
import no.nav.pdl.forvalter.dto.PdlFalskIdentitet;
import no.nav.pdl.forvalter.dto.PdlForeldreansvar;
import no.nav.pdl.forvalter.dto.PdlInnflytting;
import no.nav.pdl.forvalter.dto.PdlKontaktadresse;
import no.nav.pdl.forvalter.dto.PdlTilrettelagtKommunikasjon;
import no.nav.pdl.forvalter.dto.PdlVergemaal;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO.PersonHendelserDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
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
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_SIKKERHETSTILTAK_URL;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_SIVILSTAND;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_SLETTING;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_STATSBORGERSKAP;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_TELEFONUMMER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_TILRETTELAGT_KOMMUNIKASJON;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_UTENLANDS_IDENTIFIKASJON_NUMMER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_UTFLYTTING;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact.PDL_VERGEMAAL;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.GAMMEL_IDENTITET;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlOrdreService {

    private static final String VIOLATION_ALIAS_EXISTS = "Utgått ident kan ikke sendes. Benytt gjeldende ident %s for denne operasjonen";

    private final DeployService deployService;
    private final PersonRepository personRepository;
    private final AliasRepository aliasRepository;
    private final MapperFacade mapperFacade;

    private static Set<String> getEksternePersoner(DbPerson dbPerson) {

        return Stream.of(
                        dbPerson.getPerson().getSivilstand().stream()
                                .filter(SivilstandDTO::isEksisterendePerson)
                                .map(SivilstandDTO::getRelatertVedSivilstand)
                                .toList(),
                        dbPerson.getPerson().getForelderBarnRelasjon().stream()
                                .filter(ForelderBarnRelasjonDTO::isEksisterendePerson)
                                .map(ForelderBarnRelasjonDTO::getRelatertPerson)
                                .toList(),
                        dbPerson.getPerson().getForeldreansvar().stream()
                                .filter(ForeldreansvarDTO::isEksisterendePerson)
                                .map(ForeldreansvarDTO::getAnsvarlig)
                                .toList(),
                        dbPerson.getPerson().getVergemaal().stream()
                                .filter(VergemaalDTO::isEksisterendePerson)
                                .map(VergemaalDTO::getVergeIdent)
                                .toList(),
                        dbPerson.getPerson().getFullmakt().stream()
                                .filter(FullmaktDTO::isEksisterendePerson)
                                .map(FullmaktDTO::getMotpartsPersonident)
                                .toList(),
                        dbPerson.getPerson().getKontaktinformasjonForDoedsbo().stream()
                                .map(KontaktinformasjonForDoedsboDTO::getPersonSomKontakt)
                                .filter(Objects::nonNull)
                                .filter(KontaktinformasjonForDoedsboDTO.KontaktpersonDTO::isEksisterendePerson)
                                .map(KontaktinformasjonForDoedsboDTO.KontaktpersonDTO::getIdentifikasjonsnummer)
                                .toList())
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public OrdreResponseDTO send(String ident, Boolean isTpsMaster) {

        checkAlias(ident);

        var dbPerson = personRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException(String.format("Ident %s finnes ikke i databasen", ident)));

        var eksternePersoner = getEksternePersoner(dbPerson);

        return OrdreResponseDTO.builder()
                .hovedperson(PersonHendelserDTO.builder()
                        .ident(dbPerson.getIdent())
                        .ordrer(sendAlleInformasjonselementer(dbPerson, isNotTrue(isTpsMaster))
                                .collectList()
                                .block())
                        .build())
                .relasjoner(dbPerson.getRelasjoner().stream()
                        .filter(relasjon -> GAMMEL_IDENTITET != relasjon.getRelasjonType())
                        .filter(relasjon -> eksternePersoner.stream()
                                .noneMatch(ekstern -> ekstern.equals(relasjon.getRelatertPerson().getIdent())))
                        .map(relasjon -> PersonHendelserDTO.builder()
                                .ident(relasjon.getRelatertPerson().getIdent())
                                .ordrer(sendAlleInformasjonselementer(relasjon.getRelatertPerson(), true)
                                        .collectList()
                                        .block())
                                .build())
                        .toList())
                .build();
    }

    private void checkAlias(String ident) {

        var alias = aliasRepository.findByTidligereIdent(ident);
        if (alias.isPresent()) {
            throw new InvalidRequestException(
                    format(VIOLATION_ALIAS_EXISTS, alias.get().getPerson().getIdent()));
        }
    }

    private Flux<OrdreResponseDTO.PdlStatusDTO> sendAlleInformasjonselementer(DbPerson person, boolean skalSlettes) {

        return deployService.sendOrders(
                Stream.of(
                        conditionalDelete(person.getIdent(), skalSlettes),
                        deployService.createOrder(PDL_OPPRETT_PERSON, person.getIdent(), List.of(HistoriskIdent.builder().identer(person.getAlias().stream().map(DbAlias::getTidligereIdent).collect(Collectors.toList())).build())),
                        deployService.createOrder(PDL_NAVN, person.getIdent(), person.getPerson().getNavn()),
                        deployService.createOrder(PDL_KJOENN, person.getIdent(), person.getPerson().getKjoenn()),
                        deployService.createOrder(PDL_FOEDSEL, person.getIdent(), person.getPerson().getFoedsel()),
                        deployService.createOrder(PDL_FOLKEREGISTER_PERSONSTATUS, person.getIdent(), person.getPerson().getFolkeregisterPersonstatus()),
                        deployService.createOrder(PDL_STATSBORGERSKAP, person.getIdent(), person.getPerson().getStatsborgerskap()),
                        deployService.createOrder(PDL_KONTAKTADRESSE, person.getIdent(), mapperFacade.mapAsList(person.getPerson().getKontaktadresse(), PdlKontaktadresse.class)),
                        deployService.createOrder(PDL_BOSTEDADRESSE, person.getIdent(), person.getPerson().getBostedsadresse()),
                        deployService.createOrder(PDL_OPPHOLDSADRESSE, person.getIdent(), person.getPerson().getOppholdsadresse()),
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
                        deployService.createOrder(PDL_ADRESSEBESKYTTELSE, person.getIdent(), person.getPerson().getAdressebeskyttelse()),
                        deployService.createOrder(PDL_DOEDFOEDT_BARN, person.getIdent(), person.getPerson().getDoedfoedtBarn()),
                        deployService.createOrder(PDL_SIKKERHETSTILTAK_URL, person.getIdent(), person.getPerson().getSikkerhetstiltak())
                ).reduce(new ArrayList(), (acc, next) -> {
                    acc.addAll(next);
                    return acc;
                }));
    }

    private List<Ordre> conditionalDelete(String ident, boolean skalSlettes) {

        return skalSlettes ? deployService.createOrder(PDL_SLETTING, ident, List.of(new PdlDelete())) : Collections.emptyList();
    }
}