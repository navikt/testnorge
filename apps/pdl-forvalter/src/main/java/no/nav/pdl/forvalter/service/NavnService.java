package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.dto.RsNavn;
import no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class NavnService extends PdlArtifactService<RsNavn> {

    private static final String NAVN_INVALID_ERROR = "Navn er ikke liste over gyldige verdier";
    private final GenererNavnServiceConsumer genererNavnServiceConsumer;

    private static String blankCheck(String value, String defaultValue) {
        return isNotBlank(value) ? value : defaultValue;
    }

    @Override
    protected void validate(RsNavn pdlNavn) {

        if ((isNotBlank(pdlNavn.getFornavn()) ||
                isNotBlank(pdlNavn.getMellomnavn()) ||
                isNotBlank(pdlNavn.getEtternavn())) &&
                !genererNavnServiceConsumer.verifyNavn(NavnDTO.builder()
                        .adjektiv(pdlNavn.getFornavn())
                        .adverb(pdlNavn.getMellomnavn())
                        .substantiv(pdlNavn.getEtternavn())
                        .build())) {

            throw new HttpClientErrorException(BAD_REQUEST, NAVN_INVALID_ERROR);
        }
    }

    @Override
    protected void handle(RsNavn pdlNavn) {

        if (isBlank(pdlNavn.getFornavn()) || isBlank(pdlNavn.getEtternavn()) ||
                (isBlank(pdlNavn.getMellomnavn()) && isTrue(pdlNavn.getHasMellomnavn()))) {

            var navn = genererNavnServiceConsumer.getNavn(1);
            if (navn.isPresent()) {
                pdlNavn.setFornavn(blankCheck(pdlNavn.getFornavn(), navn.get().getAdjektiv()));
                pdlNavn.setEtternavn(blankCheck(pdlNavn.getEtternavn(), navn.get().getSubstantiv()));
                pdlNavn.setMellomnavn(blankCheck(pdlNavn.getMellomnavn(),
                        isTrue(pdlNavn.getHasMellomnavn()) ? navn.get().getAdverb() : null));
            }
        }
    }

    @Override
    protected void enforceIntegrity(List<RsNavn> type) {

        // Ingen listeintegritet å ivareta
    }
}
