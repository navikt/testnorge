package no.nav.registre.testnorge.generernavnservice.service;

import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class VerifyNavnServiceTest {

    private final String GYLDIG_ADJEKTIV = "Lojal Avansert";
    private final String GYLDIG_ADVERB = "Reflekterende";
    private final String GYLDIG_SUBSTANTIV = "Reise Løveflokk Tragedie";

    private final String IKKE_ADJEKTIV = "Aaaaaa Bbbbbbb Cccccccc";
    private final String IKKE_ADVERB = "Ddddddddd";
    private final String IKKE_SUBSTANTIV = "Eeeeeeee Fffffffff Ggggggggg Hhhhhhhhh";

    @InjectMocks
    private VerifyNavnService verifyNavnService;
    private NavnDTO gyldigNavnDTO;
    private NavnDTO ikkeGyldigNavnDTO;

    @BeforeEach
    void setUp() {
        gyldigNavnDTO = NavnDTO.builder()
                .adjektiv(GYLDIG_ADJEKTIV)
                .adverb(GYLDIG_ADVERB)
                .substantiv(GYLDIG_SUBSTANTIV)
                .build();

        ikkeGyldigNavnDTO = NavnDTO.builder()
                .adjektiv(IKKE_ADJEKTIV)
                .adverb(IKKE_ADVERB)
                .substantiv(IKKE_SUBSTANTIV)
                .build();
    }

    @Test
    void testVerifyNavn() {
        var gyldigNavnResult = verifyNavnService.verifyNavn(gyldigNavnDTO);
        var ikkeGyldigNavnResult = verifyNavnService.verifyNavn(ikkeGyldigNavnDTO);

        assertThat(gyldigNavnResult, is(Boolean.TRUE));
        assertThat(ikkeGyldigNavnResult, is(Boolean.FALSE));
    }
}