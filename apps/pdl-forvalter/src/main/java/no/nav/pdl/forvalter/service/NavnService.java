package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;

import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class NavnService extends PdlArtifactService<NavnDTO> {

    private static final String NAVN_INVALID_ERROR = "Navn er ikke i liste over gyldige verdier";
    private final GenererNavnServiceConsumer genererNavnServiceConsumer;

    private static String blankCheck(String value, String defaultValue) {
        return isNotBlank(value) ? value : defaultValue;
    }

    @Override
    protected void validate(NavnDTO navn) {

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

    @Override
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

    @Override
    protected void enforceIntegrity(List<NavnDTO> type) {

        // Ingen listeintegritet å ivareta
    }
}
