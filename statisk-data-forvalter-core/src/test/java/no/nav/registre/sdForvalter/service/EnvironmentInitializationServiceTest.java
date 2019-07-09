package no.nav.registre.sdForvalter.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import no.nav.registre.sdForvalter.consumer.rs.AaregConsumer;
import no.nav.registre.sdForvalter.consumer.rs.EregMapperConsumer;
import no.nav.registre.sdForvalter.consumer.rs.HodejegerenConsumer;
import no.nav.registre.sdForvalter.consumer.rs.KrrConsumer;
import no.nav.registre.sdForvalter.consumer.rs.SamConsumer;
import no.nav.registre.sdForvalter.consumer.rs.SkdConsumer;
import no.nav.registre.sdForvalter.consumer.rs.TpConsumer;
import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.repository.AaregRepository;
import no.nav.registre.sdForvalter.database.repository.EregRepository;
import no.nav.registre.sdForvalter.database.repository.KrrRepository;
import no.nav.registre.sdForvalter.database.repository.TpsRepository;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ContextConfiguration(classes = {
        EnvironmentInitializationService.class,
        AaregConsumer.class,
        SkdConsumer.class,
        KrrConsumer.class,
        HodejegerenConsumer.class,
        TpConsumer.class,
        SamConsumer.class,
        EregMapperConsumer.class,
        AaregRepository.class,
        TpsRepository.class,
        KrrRepository.class,
        EregRepository.class

})
public class EnvironmentInitializationServiceTest {

    private static final String ENV = "t1";

    @Mock
    AaregRepository aaregRepository;
    @Mock
    TpsRepository tpsRepository;
    @Mock
    KrrRepository krrRepository;
    @Mock
    EregRepository eregRepository;

    @Mock
    private AaregConsumer aaregConsumer;

    @Mock
    SkdConsumer skdConsumer;
    @Mock
    KrrConsumer krrConsumer;
    @Mock
    HodejegerenConsumer hodejegerenConsumer;
    @Mock
    TpConsumer tpConsumer;
    @Mock
    SamConsumer samConsumer;
    @Mock
    EregMapperConsumer eregMapperConsumer;

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