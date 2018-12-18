package no.nav.registre.orkestratoren.service;

import static java.time.LocalDate.now;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.registre.orkestratoren.consumer.rs.ArenaInntektSyntConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;

@RunWith(MockitoJUnitRunner.class)
public class ArenaInntektSyntPakkenServiceTest {

    @Mock
    private ArenaInntektSyntConsumer arenaInntektSyntConsumer;
    @Mock
    private SyntDataInfoService syntDataInfoService;

    @InjectMocks
    private ArenaInntektSyntPakkenService inntektSyntPakkenService;

    /**
     * Testscenario: De FNR som er eldre enn 13 år i første halvdel av lista fra TPSF skdavspillergruppa,
     * skal sendes til Inntektsynt for å få inntektsmelding og skal returneres fra metoden.
     */
    @Test
    public void genererEnInntektsmeldingPerFnrIInntektstub() {
        DateTimeFormatter tilFnrEtter2000 = DateTimeFormatter.ofPattern("ddMMyy60000");
        List<String> foedselsnumre = new ArrayList<>();
        foedselsnumre.add(now().format(tilFnrEtter2000));
        String personOver13Aar = now().minusYears(14).format(tilFnrEtter2000);
        foedselsnumre.add(personOver13Aar);
        foedselsnumre.add(now().minusYears(80).format(tilFnrEtter2000));
        foedselsnumre.add(now().minusYears(80).format(tilFnrEtter2000));

        when(syntDataInfoService.opprettListenLevendeNordmenn(123L)).thenReturn(foedselsnumre);

        List<String> inntektsmldMottakere = inntektSyntPakkenService.genererEnInntektsmeldingPerFnrIInntektstub(
                new SyntetiserInntektsmeldingRequest(123L));

        assertEquals(1, inntektsmldMottakere.size());
        assertEquals(personOver13Aar, inntektsmldMottakere.get(0)); //blant første halvpart av FNR-lista er det kun element 0 som er over 13 år

        verify(arenaInntektSyntConsumer).genererEnInntektsmeldingPerFnrIInntektstub(eq(Arrays.asList(personOver13Aar)));
    }
}