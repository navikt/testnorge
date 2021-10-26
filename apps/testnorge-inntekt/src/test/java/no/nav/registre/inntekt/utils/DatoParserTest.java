package no.nav.registre.inntekt.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.inntekt.domain.inntektstub.RsInntekt;

@ExtendWith(MockitoExtension.class)
public class DatoParserTest {

    private List<RsInntekt> inntekterMedHoyereAar;
    private List<RsInntekt> inntekterMedHoyereMaaned;
    //    private List<RsInntekt> inntekterMedSammeAar

    @BeforeEach
    public void setUp() {
        RsInntekt.builder()
                .aar("2017")
                .maaned("august")
                .build();
        inntekterMedHoyereAar = new ArrayList<>(Arrays.asList(
                RsInntekt.builder()
                        .aar("2016")
                        .maaned("august")
                        .build(),
                RsInntekt.builder()
                        .aar("2017")
                        .maaned("august")
                        .build(),
                RsInntekt.builder()
                        .aar("2017")
                        .maaned("august")
                        .build()));

        inntekterMedHoyereMaaned = new ArrayList<>(Arrays.asList(
                RsInntekt.builder()
                        .aar("2017")
                        .maaned("august")
                        .build(),
                RsInntekt.builder()
                        .aar("2017")
                        .maaned("september")
                        .build(),
                RsInntekt.builder()
                        .aar("2017")
                        .maaned("august")
                        .build()));
    }

    @Test
    public void finnSenesteInntektMedSenereAarTest() {
        List<RsInntekt> inntekter = DatoParser.finnSenesteInntekter(inntekterMedHoyereAar);
        assertThat(inntekter, containsInAnyOrder(inntekterMedHoyereAar.get(1), inntekterMedHoyereAar.get(2)));
    }

    @Test
    public void finnSenesteInntektMedSenereMaanedTest() {
        List<RsInntekt> inntekter = DatoParser.finnSenesteInntekter(inntekterMedHoyereMaaned);
        assertThat(inntekter, containsInAnyOrder(inntekterMedHoyereMaaned.get(1)));
    }
}