package no.nav.identpool.ident.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Rekvireringsstatus;
import no.nav.identpool.ident.repository.IdentRepository;

@RunWith(MockitoJUnitRunner.class)
public class IdentpoolServiceTest {

/*    @Mock
    private IdentRepository identRepository;

    @InjectMocks
    private IdentpoolService identpoolService;

    @Test
    public void findIdenterMedFNR() {
        identpoolService.findIdents(1, Identtype.FNR);

//        verify(identRepository, times(1)).findByRekvireringsstatusAndIdenttype(Rekvireringsstatus.LEDIG.getStatus(), Identtype.FNR.getType());
//        verify(identRepository, times(0)).findByRekvireringsstatusAndIdenttype(Rekvireringsstatus.LEDIG.getStatus(), Identtype.DNR.getType());
//        verify(identRepository, times(0)).findByRekvireringsstatus(Rekvireringsstatus.LEDIG.getStatus());
    }

    @Test
    public void findIdenterMedDNR() {
        identpoolService.findIdents(1, Identtype.DNR);

//        verify(identRepository, times(0)).findByRekvireringsstatusAndIdenttype(Rekvireringsstatus.LEDIG.getStatus(), Identtype.FNR.getType());
//        verify(identRepository, times(1)).findByRekvireringsstatusAndIdenttype(Rekvireringsstatus.LEDIG.getStatus(), Identtype.DNR.getType());
//        verify(identRepository, times(0)).findByRekvireringsstatus(Rekvireringsstatus.LEDIG.getStatus());
    }

    @Test
    public void findIdenterUtenBestemtIdenttype() {
        identpoolService.findIdents(1, Identtype.UBESTEMT);

//        verify(identRepository, times(0)).findByRekvireringsstatusAndIdenttype(Rekvireringsstatus.LEDIG.getStatus(), Identtype.FNR.getType());
//        verify(identRepository, times(0)).findByRekvireringsstatusAndIdenttype(Rekvireringsstatus.LEDIG.getStatus(), Identtype.DNR.getType());
//        verify(identRepository, times(1)).findByRekvireringsstatus(Rekvireringsstatus.LEDIG.getStatus());
    } */
}