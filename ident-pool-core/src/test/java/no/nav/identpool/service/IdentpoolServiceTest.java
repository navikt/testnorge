package no.nav.identpool.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.identpool.domain.Ident;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Kjoenn;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.repository.IdentRepository;
import no.nav.identpool.test.mockito.MockitoExtension;

@ExtendWith(MockitoExtension.class) class IdentpoolServiceTest {

    @Mock
    IdentRepository repository;

    @InjectMocks
    IdentpoolService identpoolService;

    @Test
    void hentLedigeFNRFoedtMellom() {
        List<Ident> identer = new ArrayList<>();
        Ident id = new Ident();
        id.setRekvireringsstatus(Rekvireringsstatus.LEDIG);
        id.setFoedselsdato(LocalDate.of(1992, 3, 1));
        id.setIdenttype(Identtype.FNR);
        id.setKjoenn(Kjoenn.MANN);
        id.setPersonidentifikator("123");
        identer.add(id);
        when(repository.findByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatus(LocalDate.of(1991, 1, 1), LocalDate.of(2000, 1, 1), Identtype.FNR, Rekvireringsstatus.LEDIG)).
                thenReturn(identer);
        List<String> ids = identpoolService.hentLedigeFNRFoedtMellom(LocalDate.of(1991, 1, 1), LocalDate.of(2000, 1, 1));
        assertFalse(ids.isEmpty());
        assertEquals("123", ids.get(0));
    }
}