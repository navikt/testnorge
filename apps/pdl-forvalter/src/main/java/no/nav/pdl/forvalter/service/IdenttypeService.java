package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.domain.Identtype;
import no.nav.pdl.forvalter.domain.PdlIdentRequest;
import no.nav.pdl.forvalter.domain.PdlKjoenn.Kjoenn;
import no.nav.pdl.forvalter.domain.PdlPerson;
import no.nav.pdl.forvalter.dto.RsPersonRequest;
import no.nav.pdl.forvalter.dto.RsPersonRequest.NyttNavn;
import no.nav.pdl.forvalter.utils.DatoFraIdentUtility;
import no.nav.pdl.forvalter.utils.IdenttypeFraIdentUtility;
import no.nav.pdl.forvalter.utils.KjoennFraIdentUtility;
import no.nav.pdl.forvalter.utils.SyntetiskFraIdentUtility;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.database.model.RelasjonType.GAMMEL_IDENTITET;
import static no.nav.pdl.forvalter.database.model.RelasjonType.NY_IDENTITET;
import static no.nav.pdl.forvalter.domain.Identtype.BOST;
import static no.nav.pdl.forvalter.domain.Identtype.DNR;
import static no.nav.pdl.forvalter.domain.Identtype.FNR;
import static no.nav.pdl.forvalter.domain.PdlKjoenn.Kjoenn.KVINNE;
import static no.nav.pdl.forvalter.domain.PdlKjoenn.Kjoenn.MANN;
import static no.nav.pdl.forvalter.domain.PdlKjoenn.Kjoenn.UKJENT;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class IdenttypeService {

    private static final LocalDateTime START_OF_ERA = LocalDateTime.of(1900, 1, 1, 0, 0);
    private static final String VALIDATION_DATO_INVALID = "Identtype ugyldig forespørsel: støttet datointervall " +
            "er fødsel mellom 1.1.1900 og dagens dato";
    private static final String VALIDATION_DATO_INTERVAL_INVALID = "Identtype ugyldig forespørsel: fødtFør kan ikke være tidligere " +
            "enn fødtEtter";
    private static final String VALIDATION_IDENTTYPE_INVALID = "Identtype må være en av FNR, DNR eller BOST";
    private static final String VALIDATION_ALDER_NOT_ALLOWED = "Alder må være mellom 0 og 120 år";

    private static final Random secureRandom = new SecureRandom();

    private final CreatePersonService createPersonService;
    private final RelasjonService relasjonService;
    private final SwopIdentsService swopIdentsService;
    private final MapperFacade mapperFacade;

    private static Identtype getIdenttype(PdlIdentRequest request, String ident) {

        if (nonNull(request.getIdenttype())) {
            return request.getIdenttype();
        }
        return nonNull(ident) ? IdenttypeFraIdentUtility.getIdenttype(ident) : FNR;
    }

    private static Kjoenn getKjoenn(PdlIdentRequest request, String ident) {

        if (nonNull(request.getKjoenn()) && request.getKjoenn() != UKJENT) {
            return request.getKjoenn();
        }
        return nonNull(ident) ? KjoennFraIdentUtility.getKjoenn(ident) :
                secureRandom.nextBoolean() ? MANN : KVINNE;
    }

    private static LocalDateTime getFoedtFoer(PdlIdentRequest request, String ident) {

        if (nonNull(request.getFoedtFoer())) {
            return request.getFoedtFoer();
        } else if (nonNull(request.getAlder())) {
            return LocalDateTime.now().minusYears(request.getAlder()).minusMonths(3);
        }
        return (nonNull(ident)) ? DatoFraIdentUtility.getDato(ident).plusMonths(1).atStartOfDay() :
                LocalDateTime.now().minusYears(18);
    }

    private static LocalDateTime getFoedtEtter(PdlIdentRequest request, String ident) {

        if (nonNull(request.getFoedtEtter())) {
            return request.getFoedtEtter();
        } else if (nonNull(request.getAlder())) {
            return LocalDateTime.now().minusYears(request.getAlder()).minusYears(1);
        }
        return (nonNull(ident)) ? DatoFraIdentUtility.getDato(ident).minusMonths(1).atStartOfDay() :
                LocalDateTime.now().minusYears(67);
    }

    private static boolean isSyntetisk(PdlIdentRequest request, String ident) {
        return nonNull(request.getSyntetisk()) ? request.getSyntetisk() :
                SyntetiskFraIdentUtility.isSyntetisk(ident);
    }

    public String convert(PdlPerson person) {

        var ident = person.getIdent();
        for (var type : person.getNyident()) {

            if (type.isNew()) {
                validate(type);

                ident = handle(type, person);
                if (isBlank(type.getKilde())) {
                    type.setKilde("Dolly");
                }
            }
        }
        return ident;
    }

    private void validate(PdlIdentRequest request) {

        if (nonNull(request.getIdenttype()) & FNR != request.getIdenttype() &&
                DNR != request.getIdenttype() && BOST != request.getIdenttype()) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_IDENTTYPE_INVALID);
        }

        if (nonNull(request.getFoedtEtter()) && (START_OF_ERA.isAfter(request.getFoedtEtter()) ||
                LocalDateTime.now().isBefore(request.getFoedtEtter()))) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_DATO_INVALID);
        }

        if (nonNull(request.getFoedtFoer()) && (START_OF_ERA.isAfter(request.getFoedtFoer()) ||
                LocalDateTime.now().isBefore(request.getFoedtFoer()))) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_DATO_INVALID);
        }

        if (nonNull(request.getFoedtFoer()) && nonNull(request.getFoedtFoer()) &&
                request.getFoedtEtter().isAfter(request.getFoedtFoer())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_DATO_INTERVAL_INVALID);
        }

        if (nonNull(request.getAlder()) && (request.getAlder() < 0 || request.getAlder() > 120)) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_ALDER_NOT_ALLOWED);
        }
    }

    private String handle(PdlIdentRequest request, PdlPerson person) {

        var nyPerson = createPersonService.execute(RsPersonRequest.builder()
                .identtype(getIdenttype(request, person.getIdent()))
                .kjoenn(getKjoenn(request, person.getIdent()))
                .foedtEtter(getFoedtEtter(request, person.getIdent()))
                .foedtFoer(getFoedtFoer(request, person.getIdent()))
                .nyttNavn(mapperFacade.map(request.getNyttNavn(), NyttNavn.class))
                .syntetisk(isSyntetisk(request, person.getIdent()))
                .build());

        swopIdentsService.execute(person.getIdent(), nyPerson.getIdent(), nonNull(request.getNyttNavn()));

        relasjonService.setRelasjoner(nyPerson.getIdent(), NY_IDENTITET, person.getIdent(), GAMMEL_IDENTITET);

        person.setFoedsel(nyPerson.getFoedsel());
        person.setKjoenn(nyPerson.getKjoenn());
        if (!nyPerson.getNavn().isEmpty()) {
            person.setNavn(nyPerson.getNavn());
        }
        person.setNyident(null);

        return nyPerson.getIdent();
    }
}
