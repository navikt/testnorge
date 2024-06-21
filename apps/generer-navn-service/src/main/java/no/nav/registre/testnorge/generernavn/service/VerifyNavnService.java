package no.nav.registre.testnorge.generernavn.service;

import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class VerifyNavnService {
    private static final List<String> ADJEKTIVER = FileReader.readLinesFromResources("static/adjektiv.csv");
    private static final List<String> ADVERBER = FileReader.readLinesFromResources("static/adverb.csv");
    private static final List<String> SUBSTANTIVER = FileReader.readLinesFromResources("static/substantiv.csv");

    public boolean verifyNavn(NavnDTO navnDTO) {

        var alleAdjektiv = navnDTO.getAdjektiv().split(" ");
        var alleAdverb = isNotBlank(navnDTO.getAdverb()) ? navnDTO.getAdverb().split(" ") : new String[]{};
        var alleSubstantiv = navnDTO.getSubstantiv().split(" ");

        return Arrays.stream(alleAdjektiv).allMatch(adjektiv -> verify(adjektiv, ADJEKTIVER)) &&
                Arrays.stream(alleAdverb).allMatch(adverb -> verify(adverb, ADVERBER)) &&
                Arrays.stream(alleSubstantiv).allMatch(substantiv -> verify(substantiv, SUBSTANTIVER));
    }

    private static boolean verify(String value, List<String> approvedValues) {

        return isNull(value) || approvedValues.stream().anyMatch(validValue -> validValue.equalsIgnoreCase(value));
    }
}