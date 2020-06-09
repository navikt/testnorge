package no.nav.registre.inntekt.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.inntekt.domain.RsInntekt;

@RunWith(MockitoJUnitRunner.class)
public class DatoParserTest {

    private List<RsInntekt> inntekterMedHoyereAar;
    private List<RsInntekt> inntekterMedHoyereMaaned;
    //    private List<RsInntekt> inntekterMedSammeAar

    @Before
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