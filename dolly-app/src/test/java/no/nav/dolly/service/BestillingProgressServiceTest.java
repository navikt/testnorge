package no.nav.dolly.service;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingProgressRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BestillingProgressServiceTest {

    @Mock
    private BestillingProgressRepository mockRepo;

    @InjectMocks
    private BestillingProgressService progressService;

    @Test
    public void happyPath(){

    }

    @Test(expected = NotFoundException.class)
    public void bestillingProgressKasterExceptionHvisManIkkeFinnerProgress() throws Exception{
        when(mockRepo.findBestillingProgressByBestillingId(any())).thenReturn(null);
        progressService.fetchBestillingProgressByBestillingsIdFromDB(null);
    }

    @Test
    public void hviFetchBestillingProgressFinnerObjectSåReturnerObjektet() {
        BestillingProgress mock = Mockito.mock(BestillingProgress.class);
        when(mockRepo.findBestillingProgressByBestillingId(1l)).thenReturn(Arrays.asList(mock));
        List<BestillingProgress> bes = progressService.fetchBestillingProgressByBestillingsIdFromDB(1l);
        assertThat(bes.get(0), is(mock));
    }

    @Test
    public void hvisFetchProgressSomReturnererTomListeHvisIkkeFunnetIkkeFinnerObjektSåReturnerTomListe(){
        when(mockRepo.findBestillingProgressByBestillingId(1l)).thenReturn(new ArrayList<>());
        List<BestillingProgress> bes = progressService.fetchProgressButReturnEmptyListIfBestillingsIdIsNotFound(1l);
        assertThat(bes.isEmpty(), is(true));
    }

}