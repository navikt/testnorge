package no.nav.dolly.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultSet.RsBruker;
import no.nav.dolly.repository.BrukerRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BrukerServiceTest {

    @Mock
    private BrukerRepository brukerRepository;

    @Mock
    private TeamService teamService;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private BrukerService service;

    @Test
    public void opprettBruker_kallerRepositorySave(){
        RsBruker b = new RsBruker();

//        when(mapperFacade.map(b, Bruker.class)).thenReturn(new Bruker());
        service.opprettBruker(new RsBruker());
        verify(brukerRepository).save(any());
    }

    @Test
    public void fetchBruker_kasterIkkeExceptionOrReturnererBrukerHvisBrukerErFunnet() {

    }
}