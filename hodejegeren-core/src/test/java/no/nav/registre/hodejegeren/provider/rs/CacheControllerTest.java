package no.nav.registre.hodejegeren.provider.rs;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.registre.hodejegeren.service.CacheService;

@RunWith(MockitoJUnitRunner.class)
public class CacheControllerTest {

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private CacheController cacheController;

    @Test
    public void shouldOppdatereCacheTilGruppe() {
        var avspillergruppeId = 123L;
        cacheController.oppdaterGruppeCache(123L);
        verify(cacheService).oppdaterAlleCacher(avspillergruppeId);
    }

    @Test
    public void shouldOppdatereAlleCacher() {
        cacheController.oppdaterAlleCaches();
        verify(cacheService).oppdaterAlleCacher();
    }
}