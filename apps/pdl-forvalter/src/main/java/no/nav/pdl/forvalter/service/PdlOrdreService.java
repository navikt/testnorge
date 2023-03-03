package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.database.model.DbAlias;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.AliasRepository;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.dto.FolkeregisterPersonstatus;
import no.nav.pdl.forvalter.dto.OpprettIdent;
import no.nav.pdl.forvalter.dto.OpprettRequest;
import no.nav.pdl.forvalter.dto.Ordre;
import no.nav.pdl.forvalter.dto.OrdreRequest;
import no.nav.pdl.forvalter.dto.PdlDelete;
import no.nav.pdl.forvalter.dto.PdlFalskIdentitet;
import no.nav.pdl.forvalter.dto.PdlKontaktadresse;
import no.nav.pdl.forvalter.dto.PdlSivilstand;
import no.nav.pdl.forvalter.dto.PdlTilrettelagtKommunikasjon;
import no.nav.pdl.forvalter.dto.PdlVergemaal;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
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

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.OPPHOERT;
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

    private static List<OrdreResponseDTO.PdlStatusDTO> getPersonHendelser(String ident, List<OrdreResponseDTO.PdlStatusDTO> hendelser) {

        return nonNull(hendelser) ?
                hendelser.stream()
                        .filter(hendelse -> ident.equals(hendelse.getIdent()))
                        .map(hendelse -> OrdreResponseDTO.PdlStatusDTO.builder()
                                .infoElement(hendelse.getInfoElement())
                                .hendelser(hendelse.getHendelser())
                                .build())
                        .toList() :
                emptyList();
    }

    public OrdreResponseDTO send(String ident, Boolean isTpsMaster, Boolean ekskluderEksterenePersoner) {

        var timestamp = System.currentTimeMillis();

        checkAlias(ident);

        var dbPerson = personRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException(String.format("Ident %s finnes ikke i databasen", ident)));

        var eksternePersoner = getEksternePersoner(dbPerson);

        var resultat = sendAlleInformasjonselementer(Stream.of(List.of(OpprettRequest.builder()
                                .person(dbPerson)
                                .skalSlettes(isNotTrue(isTpsMaster))
                                .build()),
                        dbPerson.getRelasjoner().stream()
                                .filter(relasjon -> isNotTrue(ekskluderEksterenePersoner) ||
                                        eksternePersoner.stream()
                                                .noneMatch(ekstern -> ekstern.equals(relasjon.getRelatertPerson().getIdent())))
                                .map(relasjon -> OpprettRequest.builder()
                                        .person(relasjon.getRelatertPerson())
                                        .skalSlettes(true)
                                        .build())
                                .toList())
                .flatMap(Collection::stream)
                .toList())
                .collectList()
                .block();

        var respons = OrdreResponseDTO.builder()
                .hovedperson(
                        PersonHendelserDTO.builder()
                                .ident(ident)
                                .ordrer(getPersonHendelser(ident, resultat))
                                .build())
                .relasjoner(dbPerson.getRelasjoner().stream()
                        .map(DbRelasjon::getRelatertPerson)
                        .map(DbPerson::getIdent)
                        .map(personIdent -> PersonHendelserDTO.builder()
                                .ident(personIdent)
                                .ordrer(getPersonHendelser(personIdent, resultat))
                                .build())
                        .toList())
                .build();

        log.info("PDL ordre for ident: {} tid: {} ms", ident, System.currentTimeMillis() - timestamp);

        return respons;
    }

    private void checkAlias(String ident) {

        var alias = aliasRepository.findByTidligereIdent(ident);
        if (alias.isPresent()) {
            throw new InvalidRequestException(
                    format(VIOLATION_ALIAS_EXISTS, alias.get().getPerson().getIdent()));
        }
    }

    private Flux<OrdreResponseDTO.PdlStatusDTO> sendAlleInformasjonselementer(List<OpprettRequest> opprettinger) {

        return deployService.sendOrders(
                OrdreRequest.builder()
                        .sletting(opprettinger.stream()
                                .filter(OpprettRequest::isSkalSlettes)
                                .map(oppretting -> deployService.createOrdre(PDL_SLETTING, oppretting.getPerson().getIdent(), List.of(new PdlDelete())))
                                .toList())
                        .oppretting(opprettinger.stream()
                                .map(oppretting ->
                                        deployService.createOrdre(PDL_OPPRETT_PERSON, oppretting.getPerson().getIdent(),
                                                List.of(OpprettIdent.builder()
                                                        .historiskeIdenter(oppretting.getPerson().getAlias().stream().map(DbAlias::getTidligereIdent).toList())
                                                        .opphoert(!oppretting.getPerson().getPerson().getFolkeregisterPersonstatus().isEmpty() &&
                                                                oppretting.getPerson().getPerson().getFolkeregisterPersonstatus().get(0).getStatus().equals(OPPHOERT))
                                                        .build())))
                                .toList())
                        .opplysninger(opprettinger.stream()
                                .map(this::getOrdrer)
                                .toList())
                        .build());
    }

    private List<Ordre> getOrdrer(OpprettRequest oppretting) {
        return Stream.of(
                        deployService.createOrdre(PDL_FOLKEREGISTER_PERSONSTATUS, oppretting.getPerson().getIdent(), mapperFacade.mapAsList(oppretting.getPerson().getPerson().getFolkeregisterPersonstatus(), FolkeregisterPersonstatus.class)),
                        deployService.createOrdre(PDL_ADRESSEBESKYTTELSE, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getAdressebeskyttelse()),
                        deployService.createOrdre(PDL_DOEDSFALL, oppretting.getPerson().getIdent(), utenHistorikk(oppretting.getPerson().getPerson().getDoedsfall())),
                        deployService.createOrdre(PDL_NAVN, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getNavn()),
                        deployService.createOrdre(PDL_KJOENN, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getKjoenn()),
                        deployService.createOrdre(PDL_FOEDSEL, oppretting.getPerson().getIdent(), utenHistorikk(oppretting.getPerson().getPerson().getFoedsel())),
                        deployService.createOrdre(PDL_STATSBORGERSKAP, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getStatsborgerskap()),
                        deployService.createOrdre(PDL_KONTAKTADRESSE, oppretting.getPerson().getIdent(), mapperFacade.mapAsList(oppretting.getPerson().getPerson().getKontaktadresse(), PdlKontaktadresse.class)),
                        deployService.createOrdre(PDL_BOSTEDADRESSE, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getBostedsadresse()),
                        deployService.createOrdre(PDL_OPPHOLDSADRESSE, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getOppholdsadresse()),
                        deployService.createOrdre(PDL_INNFLYTTING, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getInnflytting()),
                        deployService.createOrdre(PDL_UTFLYTTING, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getUtflytting()),
                        deployService.createOrdre(PDL_DELTBOSTED, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getDeltBosted()),
                        deployService.createOrdre(PDL_FORELDREANSVAR, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getForeldreansvar()),
                        deployService.createOrdre(PDL_FORELDRE_BARN_RELASJON, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getForelderBarnRelasjon()),
                        deployService.createOrdre(PDL_SIVILSTAND, oppretting.getPerson().getIdent(), mapperFacade.mapAsList(oppretting.getPerson().getPerson().getSivilstand(), PdlSivilstand.class)),
                        deployService.createOrdre(PDL_VERGEMAAL, oppretting.getPerson().getIdent(), mapperFacade.mapAsList(oppretting.getPerson().getPerson().getVergemaal(), PdlVergemaal.class)),
                        deployService.createOrdre(PDL_FULLMAKT, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getFullmakt()),
                        deployService.createOrdre(PDL_TELEFONUMMER, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getTelefonnummer()),
                        deployService.createOrdre(PDL_OPPHOLD, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getOpphold()),
                        deployService.createOrdre(PDL_KONTAKTINFORMASJON_FOR_DODESDBO, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getKontaktinformasjonForDoedsbo()),
                        deployService.createOrdre(PDL_UTENLANDS_IDENTIFIKASJON_NUMMER, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getUtenlandskIdentifikasjonsnummer()),
                        deployService.createOrdre(PDL_FALSK_IDENTITET, oppretting.getPerson().getIdent(), mapperFacade.mapAsList(utenHistorikk(oppretting.getPerson().getPerson().getFalskIdentitet()), PdlFalskIdentitet.class)),
                        deployService.createOrdre(PDL_TILRETTELAGT_KOMMUNIKASJON, oppretting.getPerson().getIdent(), mapperFacade.mapAsList(utenHistorikk(oppretting.getPerson().getPerson().getTilrettelagtKommunikasjon()), PdlTilrettelagtKommunikasjon.class)),
                        deployService.createOrdre(PDL_DOEDFOEDT_BARN, oppretting.getPerson().getIdent(), oppretting.getPerson().getPerson().getDoedfoedtBarn()),
                        deployService.createOrdre(PDL_SIKKERHETSTILTAK_URL, oppretting.getPerson().getIdent(), utenHistorikk(oppretting.getPerson().getPerson().getSikkerhetstiltak())))
                .flatMap(Collection::stream)
                .toList();
    }

    private List<? extends DbVersjonDTO> utenHistorikk(List<? extends DbVersjonDTO> artifacter) {

        return artifacter.stream()
                .max(Comparator.comparing(DbVersjonDTO::getId))
                .stream().toList();
    }
}