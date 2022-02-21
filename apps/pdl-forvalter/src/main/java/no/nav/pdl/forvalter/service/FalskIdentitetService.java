package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.consumer.GeografiskeKodeverkConsumer;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.DatoFraIdentUtility;
import no.nav.pdl.forvalter.utils.KjoennFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FalskIdentitetDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.SyntetiskFraIdentUtility.isSyntetisk;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class FalskIdentitetService implements Validation<FalskIdentitetDTO> {

    private static final String VALIDATION_UGYLDIG_INTERVAL_ERROR = "Ugyldig datointervall: gyldigFom må være før gyldigTom";
    private static final String VALIDATION_FALSK_IDENTITET_ER_FALSK_MISSING = "Falskidentitet: attribute erFalsk må oppgis";
    private static final String VALIDATION_FALSK_IDENTITET_ERROR = "Oppgitt person for falsk identitet %s ikke funnet i database";
    private static final String VALIDATION_UGYLDIG_NAVN_ERROR = "Falsik identitet: Navn er ikke i liste over gyldige verdier";
    private static final String VALIDATION_TOO_MANY_RETT_IDENTIT = "Falsk identitet: Maksimalt en av disse skal være satt: " +
            "rettIdentitetVedOpplysninger, rettIdentitetErUkjent, rettIdentitetVedIdentifikasjonsnummer eller nyFalskIdentitet";
    private static final String VALIDATION_STATSBORGERSKAP_MISSING = "Falsk identitet: statborgerskap må oppgis";

    private final PersonRepository personRepository;
    private final CreatePersonService createPersonService;
    private final RelasjonService relasjonService;
    private final GenererNavnServiceConsumer genererNavnServiceConsumer;
    private final GeografiskeKodeverkConsumer geografiskeKodeverkConsumer;

    protected static <T> int count(T artifact) {
        return nonNull(artifact) ? 1 : 0;
    }

    private static String blankCheck(String value, String defaultValue) {
        return isNotBlank(value) ? value : defaultValue;
    }

    private static boolean isNavnUpdateRequired(FalskIdentitetDTO.FalsktNavnDTO navn) {
        return isBlank(navn.getFornavn()) || isBlank(navn.getEtternavn()) ||
                (isBlank(navn.getMellomnavn()) && isTrue(navn.getHasMellomnavn()));
    }

    public List<FalskIdentitetDTO> convert(PersonDTO person) {

        for (var type : person.getFalskIdentitet()) {

            if (isTrue(type.getIsNew())) {

                handle(type, person.getIdent());
                type.setKilde(isNotBlank(type.getKilde()) ? type.getKilde() : "Dolly");
                type.setMaster(nonNull(type.getMaster()) ? type.getMaster() : Master.FREG);
            }
        }
        return person.getFalskIdentitet();
    }

    @Override
    public void validate(FalskIdentitetDTO identitet) {

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

        if (isNotBlank(identitet.getRettIdentitetVedIdentifikasjonsnummer()) &&
                !personRepository.existsByIdent(identitet.getRettIdentitetVedIdentifikasjonsnummer())) {
            throw new InvalidRequestException(
                    format(VALIDATION_FALSK_IDENTITET_ERROR, identitet.getRettIdentitetVedIdentifikasjonsnummer()));
        }

        if (nonNull(identitet.getRettIdentitetVedOpplysninger()) &&
                nonNull(identitet.getRettIdentitetVedOpplysninger().getPersonnavn()) &&
                isFalse(genererNavnServiceConsumer.verifyNavn(no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO.builder()
                        .adjektiv(identitet.getRettIdentitetVedOpplysninger().getPersonnavn().getFornavn())
                        .adverb(identitet.getRettIdentitetVedOpplysninger().getPersonnavn().getMellomnavn())
                        .substantiv(identitet.getRettIdentitetVedOpplysninger().getPersonnavn().getEtternavn())
                        .build()))) {
            throw new InvalidRequestException(VALIDATION_UGYLDIG_NAVN_ERROR);
        }

        if (nonNull(identitet.getRettIdentitetVedOpplysninger()) &&
                isNull(identitet.getRettIdentitetVedOpplysninger().getStatsborgerskap())) {
            throw new InvalidRequestException(VALIDATION_STATSBORGERSKAP_MISSING);
        }
    }

    private void handle(FalskIdentitetDTO identitet, String ident) {

        if (isTrue(identitet.getRettIdentitetErUkjent())) {
            return;

        } else if (nonNull(identitet.getRettIdentitetVedOpplysninger())) {

            if (isNull(identitet.getRettIdentitetVedOpplysninger().getPersonnavn())) {
                identitet.getRettIdentitetVedOpplysninger().setPersonnavn(new FalskIdentitetDTO.FalsktNavnDTO());
            }

            if (isNavnUpdateRequired(identitet.getRettIdentitetVedOpplysninger().getPersonnavn())) {
                var nyttNavn = genererNavnServiceConsumer.getNavn(1);
                if (nyttNavn.isPresent()) {
                    identitet.getRettIdentitetVedOpplysninger().getPersonnavn().setFornavn(
                            blankCheck(identitet.getRettIdentitetVedOpplysninger().getPersonnavn().getFornavn(),
                                    nyttNavn.get().getAdjektiv()));
                    identitet.getRettIdentitetVedOpplysninger().getPersonnavn().setEtternavn(
                            blankCheck(identitet.getRettIdentitetVedOpplysninger().getPersonnavn().getEtternavn(),
                                    nyttNavn.get().getSubstantiv()));
                    identitet.getRettIdentitetVedOpplysninger().getPersonnavn().setMellomnavn(
                            blankCheck(identitet.getRettIdentitetVedOpplysninger().getPersonnavn().getMellomnavn(),
                                    isTrue(identitet.getRettIdentitetVedOpplysninger().getPersonnavn().getHasMellomnavn()) ?
                                            nyttNavn.get().getAdverb() : null));
                }
            }
            identitet.getRettIdentitetVedOpplysninger().getPersonnavn().setHasMellomnavn(null);

            if (isNull(identitet.getRettIdentitetVedOpplysninger().getFoedselsdato())) {
                identitet.getRettIdentitetVedOpplysninger().setFoedselsdato(DatoFraIdentUtility.getDato(ident).atStartOfDay());
            }
            if (isNull(identitet.getRettIdentitetVedOpplysninger().getKjoenn())) {
                identitet.getRettIdentitetVedOpplysninger().setKjoenn(KjoennFraIdentUtility.getKjoenn(ident));
            }
            if (identitet.getRettIdentitetVedOpplysninger().getStatsborgerskap().isEmpty()) {
                identitet.getRettIdentitetVedOpplysninger().setStatsborgerskap(
                        List.of(geografiskeKodeverkConsumer.getTilfeldigLand(), "NOR"));
            }

        } else if (isBlank(identitet.getRettIdentitetVedIdentifikasjonsnummer())) {

            if (isNull(identitet.getNyFalskIdentitetPerson())) {
                identitet.setNyFalskIdentitetPerson(new PersonRequestDTO());
            }

            if (isNull(identitet.getNyFalskIdentitetPerson().getAlder()) &&
                    isNull(identitet.getNyFalskIdentitetPerson().getFoedtEtter()) &&
                    isNull(identitet.getNyFalskIdentitetPerson().getFoedtFoer())) {

                identitet.getNyFalskIdentitetPerson().setFoedtFoer(LocalDateTime.now().minusYears(18));
                identitet.getNyFalskIdentitetPerson().setFoedtEtter(LocalDateTime.now().minusYears(75));
            }

            if (isNull(identitet.getNyFalskIdentitetPerson().getSyntetisk())) {
                identitet.getNyFalskIdentitetPerson().setSyntetisk(isSyntetisk(ident));
            }

            identitet.setRettIdentitetVedIdentifikasjonsnummer(
                    createPersonService.execute(identitet.getNyFalskIdentitetPerson()).getIdent());
            relasjonService.setRelasjoner(ident, RelasjonType.FALSK_IDENTITET,
                    identitet.getRettIdentitetVedIdentifikasjonsnummer(), RelasjonType.RIKTIG_IDENTITET);
            identitet.setNyFalskIdentitetPerson(null);
        }
    }
}