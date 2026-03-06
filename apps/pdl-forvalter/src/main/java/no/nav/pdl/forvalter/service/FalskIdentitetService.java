package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.EgenskaperFraHovedperson;
import no.nav.pdl.forvalter.utils.FoedselsdatoUtility;
import no.nav.pdl.forvalter.utils.KjoennFraIdentUtility;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FalskIdentitetDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class FalskIdentitetService implements Validation<FalskIdentitetDTO> {

    private static final String VALIDATION_UGYLDIG_INTERVAL_ERROR = "Ugyldig datointervall: gyldigFom må være før gyldigTom";
    private static final String VALIDATION_FALSK_IDENTITET_ER_FALSK_MISSING = "Falskidentitet: attribute erFalsk må oppgis";
    private static final String VALIDATION_FALSK_IDENTITET_ERROR = "Oppgitt person for falsk identitet %s ikke funnet i database";
    private static final String VALIDATION_UGYLDIG_NAVN_ERROR = "Falsk identitet: Navn er ikke i liste over gyldige verdier";
    private static final String VALIDATION_TOO_MANY_RETT_IDENTIT = "Falsk identitet: Maksimalt en av disse skal være satt: " +
                                                                   "rettIdentitetVedOpplysninger, rettIdentitetErUkjent, rettIdentitetVedIdentifikasjonsnummer eller nyFalskIdentitet";
    private static final String VALIDATION_STATSBORGERSKAP_MISSING = "Falsk identitet: statborgerskap må oppgis";

    private final PersonRepository personRepository;
    private final CreatePersonService createPersonService;
    private final RelasjonService relasjonService;
    private final GenererNavnServiceConsumer genererNavnServiceConsumer;
    private final KodeverkConsumer kodeverkConsumer;

    public Mono<Void> convert(PersonDTO person) {

        return Flux.fromIterable(person.getFalskIdentitet())
                .filter(type -> isTrue(type.getIsNew()))
                .flatMap(type -> handle(type, person).thenReturn(type))
                .doOnNext(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, person));
                })
                .collectList()
                .then();
    }

    @Override
    public Mono<Void> validate(FalskIdentitetDTO identitet) {

        if (isNull(identitet.getErFalsk())) {
            throw new InvalidRequestException(VALIDATION_FALSK_IDENTITET_ER_FALSK_MISSING);
        }

        if (nonNull(identitet.getGyldigFraOgMed()) && nonNull(identitet.getGyldigTilOgMed()) &&
            !identitet.getGyldigFraOgMed().isBefore(identitet.getGyldigTilOgMed())) {
            throw new InvalidRequestException(VALIDATION_UGYLDIG_INTERVAL_ERROR);
        }

        if (count(identitet.getRettIdentitetErUkjent()) + count(identitet.getRettIdentitetVedIdentifikasjonsnummer()) +
            count(identitet.getRettIdentitetVedOpplysninger()) + count(identitet.getNyFalskIdentitetPerson()) > 1) {
            throw new InvalidRequestException(VALIDATION_TOO_MANY_RETT_IDENTIT);
        }

        if (nonNull(identitet.getRettIdentitetVedOpplysninger()) &&
            isNull(identitet.getRettIdentitetVedOpplysninger().getStatsborgerskap())) {
            throw new InvalidRequestException(VALIDATION_STATSBORGERSKAP_MISSING);
        }

        if (isNotBlank(identitet.getRettIdentitetVedIdentifikasjonsnummer())) {
            return personRepository.existsByIdent(identitet.getRettIdentitetVedIdentifikasjonsnummer())
                    .flatMap(exists -> isFalse(exists) ?
                            Mono.error(new InvalidRequestException(
                                    VALIDATION_FALSK_IDENTITET_ERROR.formatted(identitet.getRettIdentitetVedIdentifikasjonsnummer()))) :
                            Mono.empty());
        }

        if (nonNull(identitet.getRettIdentitetVedOpplysninger()) &&
            nonNull(identitet.getRettIdentitetVedOpplysninger().getPersonnavn())) {

            return genererNavnServiceConsumer.verifyNavn(no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO.builder()
                            .adjektiv(identitet.getRettIdentitetVedOpplysninger().getPersonnavn().getFornavn())
                            .adverb(identitet.getRettIdentitetVedOpplysninger().getPersonnavn().getMellomnavn())
                            .substantiv(identitet.getRettIdentitetVedOpplysninger().getPersonnavn().getEtternavn())
                            .build())
                    .flatMap(exists -> isFalse(exists) ?
                            Mono.error(new InvalidRequestException(VALIDATION_UGYLDIG_NAVN_ERROR)) :
                            Mono.empty());
        }
        return Mono.empty();
    }

    private Mono<Void> handle(FalskIdentitetDTO identitet, PersonDTO person) {

        return Mono.just(identitet)
                .flatMap(type -> {
                    if (isNotTrue(identitet.getRettIdentitetErUkjent())) {

                        if (nonNull(identitet.getRettIdentitetVedOpplysninger())) {

                            return opprettNyeOpplysningstyper(identitet, person);

                        } else if (isBlank(identitet.getRettIdentitetVedIdentifikasjonsnummer())) {

                            return opprettNyIdentitet(identitet, person);
                        }
                    }

                    if (person.getFolkeregisterPersonstatus().stream()
                                .findFirst()
                                .orElse(new FolkeregisterPersonstatusDTO())
                                .getStatus() != FolkeregisterPersonstatus.OPPHOERT) {

                        person.getFolkeregisterPersonstatus().addFirst(FolkeregisterPersonstatusDTO.builder()
                                .isNew(true)
                                .id(person.getFolkeregisterPersonstatus().stream()
                                            .max(Comparator.comparing(FolkeregisterPersonstatusDTO::getId))
                                            .orElse(FolkeregisterPersonstatusDTO.builder().id(0).build())
                                            .getId() + 1)
                                .build());
                    }

                    person.setBostedsadresse(null);
                    person.setOppholdsadresse(null);
                    person.setKontaktadresse(null);
                    person.setNavn(null);
                    person.setKjoenn(null);
                    person.setFoedselsdato(null);
                    person.setFoedested(null);
                    person.setFoedsel(null);
                    person.setSivilstand(null);
                    person.setStatsborgerskap(null);
                    return Mono.empty();
                });
    }

    private Mono<Void> opprettNyeOpplysningstyper(FalskIdentitetDTO identitet, PersonDTO person) {

        if (isNull(identitet.getRettIdentitetVedOpplysninger().getPersonnavn())) {
            identitet.getRettIdentitetVedOpplysninger().setPersonnavn(new FalskIdentitetDTO.FalsktNavnDTO());
        }

        return setNavn(identitet).thenReturn(identitet)
                .doOnNext(type -> {
                    identitet.getRettIdentitetVedOpplysninger().getPersonnavn().setHasMellomnavn(null);

                    if (isNull(identitet.getRettIdentitetVedOpplysninger().getFoedselsdato())) {
                        identitet.getRettIdentitetVedOpplysninger().setFoedselsdato(
                                FoedselsdatoUtility.getFoedselsdato(person));
                    }
                    if (isNull(identitet.getRettIdentitetVedOpplysninger().getKjoenn())) {
                        identitet.getRettIdentitetVedOpplysninger().setKjoenn(
                                person.getKjoenn().stream()
                                        .map(KjoennDTO::getKjoenn)
                                        .findFirst()
                                        .orElse(KjoennFraIdentUtility.getKjoenn(person.getIdent())));
                    }
                })
                .flatMap(type -> {
                    if (identitet.getRettIdentitetVedOpplysninger().getStatsborgerskap().isEmpty()) {

                        return kodeverkConsumer.getTilfeldigLand()
                                .doOnNext(land -> identitet.getRettIdentitetVedOpplysninger().
                                        setStatsborgerskap(List.of(land, "NOR")))
                                .then();
                    }
                    return Mono.empty();
                })
                .then();
    }

    private Mono<Void> setNavn(FalskIdentitetDTO identitet) {

        if (isNavnUpdateRequired(identitet.getRettIdentitetVedOpplysninger().getPersonnavn())) {
            return genererNavnServiceConsumer.getNavn(1)
                    .doOnNext(nyttNavn -> {

                        identitet.getRettIdentitetVedOpplysninger().getPersonnavn().setFornavn(
                                blankCheck(identitet.getRettIdentitetVedOpplysninger().getPersonnavn().getFornavn(),
                                        nyttNavn.getAdjektiv()));
                        identitet.getRettIdentitetVedOpplysninger().getPersonnavn().setEtternavn(
                                blankCheck(identitet.getRettIdentitetVedOpplysninger().getPersonnavn().getEtternavn(),
                                        nyttNavn.getSubstantiv()));
                        identitet.getRettIdentitetVedOpplysninger().getPersonnavn().setMellomnavn(
                                blankCheck(identitet.getRettIdentitetVedOpplysninger().getPersonnavn().getMellomnavn(),
                                        isTrue(identitet.getRettIdentitetVedOpplysninger().getPersonnavn().getHasMellomnavn()) ?
                                                nyttNavn.getAdverb() : null));
                    })
                    .then();
        }
        return Mono.empty();
    }

    private Mono<Void> opprettNyIdentitet(FalskIdentitetDTO identitet, PersonDTO person) {

        if (isNull(identitet.getNyFalskIdentitetPerson())) {
            identitet.setNyFalskIdentitetPerson(new PersonRequestDTO());
        }

        if (isNull(identitet.getNyFalskIdentitetPerson().getAlder()) &&
            isNull(identitet.getNyFalskIdentitetPerson().getFoedtEtter()) &&
            isNull(identitet.getNyFalskIdentitetPerson().getFoedtFoer())) {

            identitet.getNyFalskIdentitetPerson().setFoedtFoer(LocalDateTime.now().minusYears(18));
            identitet.getNyFalskIdentitetPerson().setFoedtEtter(LocalDateTime.now().minusYears(75));
        }

        EgenskaperFraHovedperson.kopierData(person, identitet.getNyFalskIdentitetPerson());

        return createPersonService.execute(identitet.getNyFalskIdentitetPerson())
                .map(DbPerson::getIdent)
                .doOnNext(identitet::setRettIdentitetVedIdentifikasjonsnummer)
                .flatMap(rettIdenitet -> relasjonService.setRelasjoner(person.getIdent(), RelasjonType.FALSK_IDENTITET,
                        identitet.getRettIdentitetVedIdentifikasjonsnummer(), RelasjonType.RIKTIG_IDENTITET).thenReturn(identitet))
                .doOnNext(identitet1 -> identitet.setNyFalskIdentitetPerson(null))
                .then();
    }

    private static String blankCheck(String value, String defaultValue) {
        return isNotBlank(value) ? value : defaultValue;
    }

    private static boolean isNavnUpdateRequired(FalskIdentitetDTO.FalsktNavnDTO navn) {

        return isBlank(navn.getFornavn()) || isBlank(navn.getEtternavn()) ||
               (isBlank(navn.getMellomnavn()) && isTrue(navn.getHasMellomnavn()));
    }

    protected static <T> int count(T artifact) {
        return nonNull(artifact) ? 1 : 0;
    }
}