package no.nav.dolly.appserivces.tpsf.service;

import no.nav.dolly.appserivces.sigrunstub.restcom.SigrunStubApiService;
import no.nav.dolly.appserivces.tpsf.restcom.TpsfApiService;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.TestgruppeService;

import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DollyTpsfServiceTest {

    @Mock
    TpsfApiService tpsfApiService;

    @Mock
    TestgruppeService testgruppeService;

    @Mock
    SigrunStubApiService sigrunStubApiService;

    @Mock
    IdentRepository identRepository;

    @Mock
    BestillingProgressRepository bestillingProgressRepository;

    @Mock
    BestillingService bestillingService;

    @InjectMocks
    DollyTpsfService dollyTpsfService;


    @Test
    public void bestillingBlirSattFerdigNaarExceptionKastes() throws Exception {

        String s;
//        when(tpsfApiService.opprettPersonerTpsf(any())).thenReturn(new ArrayList<>());
//        when(tpsfApiService.sendTilTpsFraTPSF(any(), any())).thenThrow(Exception.class);
    }
}