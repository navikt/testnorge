package no.nav.identpool.ident.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.identpool.ident.repository.IdentPredicateUtil;
import no.nav.identpool.ident.repository.IdentRepository;

@RunWith(MockitoJUnitRunner.class)
public class IdentpoolServiceTest {

    @Mock
    private IdentRepository identRepository;

    @Spy
    private IdentPredicateUtil identPredicateUtil = new IdentPredicateUtil();

    @InjectMocks
    private IdentpoolService identpoolService;

    @Before
    public void populerDatabase() {
    }

    @Test
    public void findIdenterMedFNR() {
        //        identpoolService.finnIdenter(Identtype.FNR, PageRequest.of(0, 1));
        //
        //        verify(identRepository, times(1)).findByRekvireringsstatusAndIdenttype(eq(Rekvireringsstatus.LEDIG), eq(Identtype.FNR), any());
        //        verify(identRepository, times(0)).findByRekvireringsstatusAndIdenttype(eq(Rekvireringsstatus.LEDIG), eq(Identtype.DNR), any());
        //        verify(identRepository, times(0)).findByRekvireringsstatus(eq(Rekvireringsstatus.LEDIG), any());
    }

    @Test
    public void findIdenterMedDNR() {
        //        identpoolService.finnIdenter(Identtype.DNR, PageRequest.of(0, 1));
        //
        //        verify(identRepository, times(0)).findByRekvireringsstatusAndIdenttype(eq(Rekvireringsstatus.LEDIG), eq(Identtype.FNR), any());
        //        verify(identRepository, times(1)).findByRekvireringsstatusAndIdenttype(eq(Rekvireringsstatus.LEDIG), eq(Identtype.DNR), any());
        //        verify(identRepository, times(0)).findByRekvireringsstatus(eq(Rekvireringsstatus.LEDIG), any());
    }

    @Test
    public void findIdenterUtenBestemtIdenttype() {
        //        identpoolService.finnIdenter(Identtype.UBESTEMT, PageRequest.of(0, 1));
        //
        //        verify(identRepository, times(0)).findByRekvireringsstatusAndIdenttype(eq(Rekvireringsstatus.LEDIG), eq(Identtype.FNR), any());
        //        verify(identRepository, times(0)).findByRekvireringsstatusAndIdenttype(eq(Rekvireringsstatus.LEDIG), eq(Identtype.DNR), any());
        //        verify(identRepository, times(1)).findByRekvireringsstatus(eq(Rekvireringsstatus.LEDIG), any());
    }
}