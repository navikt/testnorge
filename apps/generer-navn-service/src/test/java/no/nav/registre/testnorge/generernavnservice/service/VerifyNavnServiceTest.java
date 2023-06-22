package no.nav.registre.testnorge.generernavnservice.service;

import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class VerifyNavnServiceTest {

    private final static String GYLDIG_ADJEKTIV = "Lojal Avansert";
    private final static String GYLDIG_ADVERB = "Reflekterende";
    private final static String GYLDIG_SUBSTANTIV = "Reise Løveflokk Tragedie";

    private final static String IKKE_ADJEKTIV = "Aaaaaa Bbbbbbb Cccccccc";
    private final static String IKKE_ADVERB = "Ddddddddd";
    private final static String IKKE_SUBSTANTIV = "Eeeeeeee Fffffffff Ggggggggg Hhhhhhhhh";

    @Test
    void testVerifyNavn() {
        var service = new VerifyNavnService();
        var gyldigNavnResult = service.verifyNavn(
                NavnDTO.builder()
                        .adjektiv(GYLDIG_ADJEKTIV)
                        .adverb(GYLDIG_ADVERB)
                        .substantiv(GYLDIG_SUBSTANTIV)
                        .build()
        );
        var gyldigNavnMedTomtMellomnavnResult = service.verifyNavn(
                NavnDTO.builder()
                        .adjektiv(GYLDIG_ADJEKTIV)
                        .adverb(null)
                        .substantiv(GYLDIG_SUBSTANTIV)
                        .build()
        );
        var ikkeGyldigNavnResult = service.verifyNavn(
                NavnDTO.builder()
                        .adjektiv(IKKE_ADJEKTIV)
                        .adverb(IKKE_ADVERB)
                        .substantiv(IKKE_SUBSTANTIV)
                        .build()
        );
        assertThat(gyldigNavnResult).isTrue();
        assertThat(gyldigNavnMedTomtMellomnavnResult).isTrue();
        assertThat(ikkeGyldigNavnResult).isFalse();
    }
}