package no.nav.registre.sdForvalter.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import no.nav.registre.sdForvalter.consumer.rs.AaregConsumer;
import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.repository.AaregRepository;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class EnvironmentInitializationServiceTest {

    private static final String ENV = "t1";

    @Mock
    AaregRepository aaregRepository;

    @Mock
    private AaregConsumer aaregConsumer;

    @InjectMocks
    private EnvironmentInitializationService environmentInitializationService;

    @Test
    public void initializeEnvironmentWithStaticData() {
    }

    @Test
    public void initializeSkd() {
    }

    @Test
    public void initializeAareg() {

        HashSet<String> input = new HashSet<>();
        input.add("123");
        input.add("456");

        HashSet<String> result = new HashSet<>();
        result.add("123");

        when(aaregConsumer.finnPersonerUtenArbeidsforhold(input, ENV)).thenReturn(result);

        Set<AaregModel> expectedInput = new HashSet<>();
        expectedInput.add(new AaregModel("123", 0));

        ArrayList<AaregModel> repoOut = new ArrayList<>(expectedInput);
        repoOut.add(new AaregModel("456", 1));

        when(aaregRepository.findAll()).thenReturn(repoOut);

        environmentInitializationService.initializeAareg(ENV);

        verify(aaregConsumer).send(expectedInput, ENV);

    }

    @Test
    public void initializeKrr() {
    }
}