package no.nav.dolly.appserivces.sigrunstub.controller;

import no.nav.dolly.appserivces.sigrunstub.restcom.SigrunStubApiService;
import no.nav.dolly.domain.resultset.RsSigrunnOpprettSkattegrunnlag;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SigrunnControllerTest {

    @Mock
    SigrunStubApiService sigrunStubApiService;

    @InjectMocks
    SigrunnController controller;

    @Test
    public void opprettInntekt_happyPath(){
        RsSigrunnOpprettSkattegrunnlag grunn = new RsSigrunnOpprettSkattegrunnlag();

        controller.opprettInntekt(grunn);
        verify(sigrunStubApiService).createSkattegrunnlag(grunn);
    }
}