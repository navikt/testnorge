package no.nav.dolly.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static no.nav.dolly.domain.jpa.Testident.Master.TPSF;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IdentServiceTest {

    private static final String STANDARD_IDENTER_1 = "en";
    private static final String STANDAR_IDENTER_2 = "to";

    @Mock
    private IdentRepository identRepository;

    @Mock
    private TransaksjonMappingRepository transaksjonMappingRepository;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private IdentService identService;

    private Testgruppe testgruppe = new Testgruppe();
    private Testgruppe standardGruppe = new Testgruppe();

    @BeforeEach
    public void setup() {
        testgruppe.setId(1L);
    }

    @Test
    public void saveIdentTilGruppe_saveAvIdentInnholderInputIdentstringOgTestgruppe() {
        when(identRepository.save(any())).thenReturn(new Testident());

        identService.saveIdentTilGruppe(STANDARD_IDENTER_1, standardGruppe, TPSF, null);

        ArgumentCaptor<Testident> cap = ArgumentCaptor.forClass(Testident.class);
        verify(identRepository).save(cap.capture());

        Testident testident = cap.getValue();

        assertThat(testident.getIdent(), is(STANDARD_IDENTER_1));
        assertThat(testident.getTestgruppe(), is(standardGruppe));
    }

    @Test
    public void slettTestident_ok() {

        String ident = "1";

        identService.slettTestident(ident);

        verify(identRepository).deleteTestidentByIdent(ident);
    }

    @Test
    public void slettTestidenterByGruppeId_ok() {

        long gruppeId = 1L;

        identService.slettTestidenterByGruppeId(gruppeId);

        verify(identRepository).deleteTestidentByTestgruppeId(gruppeId);
    }
}