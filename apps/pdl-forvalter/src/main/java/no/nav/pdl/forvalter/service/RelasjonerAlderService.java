package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.utils.DatoFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class RelasjonerAlderService {

    private static final Random RANDOM = new SecureRandom();
    private final Clock clock;

    public BestillingRequestDTO fixRelasjonerAlder(BestillingRequestDTO request) {

        fixFoedsel(request);

        request.getPerson().getForelderBarnRelasjon().stream()
                .filter(ForelderBarnRelasjonDTO::isForeldre)
                .map(relasjon -> {
                    if (isNotBlank(relasjon.getRelatertPerson())) {
                        return getAlder(DatoFraIdentUtility.getDato(relasjon.getRelatertPerson()));
                    } else if (nonNull(relasjon.getNyRelatertPerson())) {
                        return getAlderNyPersonBarn(relasjon.getNyRelatertPerson());
                    } else if (nonNull(relasjon.getRelatertPersonUtenFolkeregisteridentifikator())) {
                        return getAlder(relasjon.getRelatertPersonUtenFolkeregisteridentifikator().getFoedselsdato());
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .max(Comparator.comparing(integer -> integer))
                .map(Integer::longValue)
                .ifPresent(eldsteBarn -> {
                    if (isNull(request.getAlder()) && isNull(request.getFoedtFoer())) {
                        request.setFoedtFoer(LocalDateTime.now(clock).minusYears(18 + eldsteBarn));
                        request.setFoedtEtter(request.getFoedtFoer().minusYears(18));
                    }
                    request.getPerson().getSivilstand().stream()
                            .filter(SivilstandDTO::isGiftOrSamboer)
                            .forEach(partner -> {
                                if (isNull(getAlderSivilstand(partner))) {
                                    if (isNull(partner.getNyRelatertPerson())) {
                                        partner.setNyRelatertPerson(new PersonRequestDTO());
                                    }
                                    partner.getNyRelatertPerson().setAlder(request.getAlder());
                                    partner.getNyRelatertPerson().setFoedtFoer(request.getFoedtFoer());
                                    partner.getNyRelatertPerson().setFoedtEtter(request.getFoedtEtter());
                                }
                            });
                });

        fixForeldre(request);

        return request;
    }

    private void fixForeldre(BestillingRequestDTO request) {
        request.getPerson()
                .getForelderBarnRelasjon().stream()
                .filter(ForelderBarnRelasjonDTO::isBarn)
                .filter(forelder -> request.hasAlder())
                .forEach(forelder -> {
                    if (isNull(getAlderForelder(forelder))) {
                        if (isNull(forelder.getNyRelatertPerson())) {
                            forelder.setNyRelatertPerson(new PersonRequestDTO());
                        }
                        forelder.getNyRelatertPerson().setFoedtFoer(request.getFoedtEtter().minusYears(18));
                        forelder.getNyRelatertPerson().setFoedtEtter(request.getFoedtEtter().minusYears(18).minusYears(18));
                    }
                });
    }

    private static void fixFoedsel(BestillingRequestDTO request) {
        request.getPerson().getFoedsel()
                .forEach(foedsel -> {

                    if (nonNull(foedsel.getFoedselsdato())) {
                        request.setFoedtEtter(foedsel.getFoedselsdato().minusDays(1));
                        request.setFoedtFoer(foedsel.getFoedselsdato().plusDays(1));
                    } else if (nonNull(foedsel.getFoedselsaar())) {
                        request.setFoedtEtter(LocalDateTime.of(foedsel.getFoedselsaar() - 1, 12, 31, 23, 59));
                        request.setFoedtFoer(LocalDateTime.of(foedsel.getFoedselsaar() + 1, 1, 1, 0, 0));
                    }
                });

        if (request.hasAlder() && nonNull(request.getAlder())) {
            request.setFoedtEtter(LocalDateTime.now().minusYears(request.getAlder()).minusMonths(6));
            request.setFoedtFoer(LocalDateTime.now().minusYears(request.getAlder()));
            request.setAlder(null);
        }
    }

    private Integer getAlderForelder(ForelderBarnRelasjonDTO relasjon) {

        if (isNotBlank(relasjon.getRelatertPerson())) {
            return getAlder(DatoFraIdentUtility.getDato(relasjon.getRelatertPerson()));
        } else if (nonNull(relasjon.getNyRelatertPerson())) {
            return getAlderNyPersonVoksen(relasjon.getNyRelatertPerson());
        } else if (nonNull(relasjon.getRelatertPersonUtenFolkeregisteridentifikator())) {
            return getAlder(relasjon.getRelatertPersonUtenFolkeregisteridentifikator().getFoedselsdato());
        } else {
            return null;
        }
    }

    private Integer getAlderSivilstand(SivilstandDTO relasjon) {

        if (isNotBlank(relasjon.getRelatertVedSivilstand())) {
            return getAlder(DatoFraIdentUtility.getDato(relasjon.getRelatertVedSivilstand()));
        } else if (nonNull(relasjon.getNyRelatertPerson())) {
            return getAlderNyPersonVoksen(relasjon.getNyRelatertPerson());
        } else {
            return null;
        }
    }

    private Integer getAlderNyPersonBarn(PersonRequestDTO relasjon) {

        if (nonNull(relasjon.getAlder())) {
            return relasjon.getAlder();

        } else if (isNull(relasjon.getFoedtEtter()) && isNull(relasjon.getFoedtFoer())) {
            return null;

        } else if (nonNull(relasjon.getFoedtEtter()) && nonNull(relasjon.getFoedtFoer())) {
            return Math.max(getRandomAlder(relasjon), getAlder(relasjon.getFoedtFoer()));

        } else if (nonNull(relasjon.getFoedtEtter())) {
            return getRandomAlder(relasjon);

        } else {
            return Math.max(RANDOM.nextInt(getAlder(relasjon.getFoedtFoer().minusYears(3))), getAlder(relasjon.getFoedtFoer()));
        }
    }

    private Integer getRandomAlder(PersonRequestDTO relasjon) {
        return getAlder(relasjon.getFoedtEtter()) > 0 ? RANDOM.nextInt(getAlder(relasjon.getFoedtEtter())) : 0;
    }

    private Integer getAlderNyPersonVoksen(PersonRequestDTO relasjon) {

        if (nonNull(relasjon.getAlder())) {
            return relasjon.getAlder();

        } else if (isNull(relasjon.getFoedtEtter()) && isNull(relasjon.getFoedtFoer())) {
            return null;

        } else if (nonNull(relasjon.getFoedtEtter()) && nonNull(relasjon.getFoedtFoer())) {
            return Math.max(RANDOM.nextInt(getAlder(relasjon.getFoedtEtter())), getAlder(relasjon.getFoedtFoer()));

        } else if (nonNull(relasjon.getFoedtEtter())) {
            return getAlder(relasjon.getFoedtEtter());

        } else {
            return Math.max(RANDOM.nextInt(getAlder(relasjon.getFoedtFoer().minusYears(18))), getAlder(relasjon.getFoedtFoer()));
        }
    }

    private Integer getAlder(LocalDate start) {

        return (int) ChronoUnit.YEARS.between(start, LocalDateTime.now(clock));
    }

    private Integer getAlder(LocalDateTime start) {

        return nonNull(start) ? getAlder(start.toLocalDate()) : null;
    }
}
