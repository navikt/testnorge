package no.nav.registre.ereg.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import no.nav.registre.ereg.consumer.rs.IdentPoolConsumer;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ContextConfiguration(classes = {NameService.class})
public class NameServiceTest {

    @Mock
    private IdentPoolConsumer identPoolConsumer;

    private NameService nameService;

    @Before
    public void setUp() {
        nameService = new NameService("koder.csv", identPoolConsumer);
    }

    @Test
    public void getFullNames_AS() {
        when(identPoolConsumer.getFakeNames(1)).thenReturn(Collections.singletonList("GUL BOLLE"));
        nameService.init();
        List<String> fullNames = nameService.getFullNames(Collections.singletonList("01"), "AS");
        assertEquals(1, fullNames.size());
        assertEquals("GUL BOLLE Jordbruk, tilhør. tjenester, jakt AS", fullNames.get(0));
    }
}