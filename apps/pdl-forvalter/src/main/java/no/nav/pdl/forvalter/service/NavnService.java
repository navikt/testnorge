package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.FoedselsdatoUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class NavnService implements BiValidation<NavnDTO, PersonDTO> {

    private static final String NAVN_INVALID_ERROR = "Navn er ikke i liste over gyldige verdier";
    private final GenererNavnServiceConsumer genererNavnServiceConsumer;

    private static String blankCheck(String value, String defaultValue) {
        return isNotBlank(value) ? value : defaultValue;
    }

    public List<NavnDTO> convert(PersonDTO person) {

        for (var type : person.getNavn()) {

            if (isTrue(type.getIsNew())) {

                type.setKilde(getKilde(type));
                type.setMaster(getMaster(type, person));
                handle(type);
            }
        }

        return fixGyldigFraOgMed(person);
    }

    @Override
    public void validate(NavnDTO navn, PersonDTO person) {

        if ((isNotBlank(navn.getFornavn()) ||
                isNotBlank(navn.getMellomnavn()) ||
                isNotBlank(navn.getEtternavn())) &&
                isFalse(genererNavnServiceConsumer.verifyNavn(no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO.builder()
                        .adjektiv(navn.getFornavn())
                        .adverb(navn.getMellomnavn())
                        .substantiv(navn.getEtternavn())
                        .build()))) {

            throw new InvalidRequestException(NAVN_INVALID_ERROR);
        }
    }

    protected void handle(NavnDTO navn) {

        if (isBlank(navn.getFornavn()) || isBlank(navn.getEtternavn()) ||
                (isBlank(navn.getMellomnavn()) && isTrue(navn.getHasMellomnavn()))) {

            var nyttNavn = genererNavnServiceConsumer.getNavn(1);
            if (nyttNavn.isPresent()) {
                navn.setFornavn(blankCheck(navn.getFornavn(), nyttNavn.get().getAdjektiv()));
                navn.setEtternavn(blankCheck(navn.getEtternavn(), nyttNavn.get().getSubstantiv()));
                navn.setMellomnavn(blankCheck(navn.getMellomnavn(),
                        isTrue(navn.getHasMellomnavn()) ? nyttNavn.get().getAdverb() : null));
            }
            navn.setHasMellomnavn(null);
        }
    }

    private List<NavnDTO> fixGyldigFraOgMed(PersonDTO person) {

        var foedselsdato = FoedselsdatoUtility.getFoedselsdato(person);

        var maksDato = person.getNavn().stream()
                .filter(navn -> nonNull(navn.getGyldigFraOgMed()))
                .max(Comparator.comparing(NavnDTO::getGyldigFraOgMed))
                .map(NavnDTO::getGyldigFraOgMed)
                .orElse(null);

        var version = new AtomicInteger(0);
        person.getNavn().stream()
                .filter(navn -> isNull(navn.getGyldigFraOgMed()))
                .forEach(navn -> {
                    if (nonNull(maksDato)) {
                        navn.setGyldigFraOgMed(maksDato.plusDays(version.incrementAndGet()));
                    } else {
                        navn.setGyldigFraOgMed(foedselsdato.plusDays(version.getAndIncrement()));
                    }
                });

        person.setNavn(person.getNavn().stream()
                .sorted(Comparator.comparing(NavnDTO::getGyldigFraOgMed).reversed())
                .toList());

        var version1 = new AtomicInteger(0);
        person.getNavn()
                .forEach(navn -> navn.setId(person.getNavn().size() - version1.getAndIncrement()));

        return person.getNavn();
    }
}
