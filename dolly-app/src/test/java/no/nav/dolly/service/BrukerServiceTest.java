package no.nav.dolly.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultSet.RsBruker;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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

        service.opprettBruker(new RsBruker());
        verify(brukerRepository).save(any());
    }

    @Test
    public void fetchBruker_kasterIkkeExceptionOgReturnererBrukerHvisBrukerErFunnet() {
        when(brukerRepository.findBrukerByNavIdent(any())).thenReturn(new Bruker());
        Bruker b = service.fetchBruker("test");
        assertThat(b, is(notNullValue()));
    }

    @Test(expected = NotFoundException.class)
    public void fetchBruker_kasterExceptionHvisIngenBrukerFunnet() {
        when(brukerRepository.findBrukerByNavIdent(any())).thenReturn(null);
        Bruker b = service.fetchBruker("test");
        assertThat(b, is(notNullValue()));
    }

    @Test
    public void getBruker_KallerRepoHentBrukere(){
        service.fetchBrukere();
        verify(brukerRepository).findAll();
    }
}