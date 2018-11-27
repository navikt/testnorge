package no.nav.identpool.navnepool;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import no.nav.identpool.navnepool.domain.Navn;

@RunWith(MockitoJUnitRunner.class)
public class NavnepoolServiceTest {

    private List<String> validFornavn = Arrays.asList("fornavn1", "fornavn2");
    private List<String> validEtternavn = Arrays.asList("etternavn1", "etternavn2", "etternavn3");
    @Mock
    private SecureRandom randomMock;

    private NavnepoolService navnepoolService;
    private Answer<Integer> answerIterationFornavn = new Answer<Integer>() {
        private int i = 0;

        @Override public Integer answer(InvocationOnMock invocation) throws Throwable {
            return i++;
        }
    };
    private Answer<Integer> answerIterationEtternavn = new Answer<Integer>() {
        private int i = 0;

        @Override public Integer answer(InvocationOnMock invocation) throws Throwable {
            return i++;
        }
    };

    @Test
    public void shouldGetAListOfRandomNames() {
        navnepoolService = new NavnepoolService(validFornavn, validEtternavn, randomMock);

        when(randomMock.nextInt(2)).thenAnswer(answerIterationFornavn);
        when(randomMock.nextInt(3)).thenAnswer(answerIterationEtternavn);

        int antallNavn = 2;
        List<Navn> tilfeldigeNavn = navnepoolService.hentTilfeldigeNavn(antallNavn);

        assertEquals(antallNavn, tilfeldigeNavn.size());
        assertEquals(validFornavn.get(0), tilfeldigeNavn.get(0).getFornavn());
        assertEquals(validFornavn.get(1), tilfeldigeNavn.get(1).getFornavn());
        assertEquals(validEtternavn.get(0), tilfeldigeNavn.get(0).getEtternavn());
        assertEquals(validEtternavn.get(1), tilfeldigeNavn.get(1).getEtternavn());
    }
}