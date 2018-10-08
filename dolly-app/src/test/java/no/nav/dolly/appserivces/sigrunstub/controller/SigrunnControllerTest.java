package no.nav.dolly.appserivces.sigrunstub.controller;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.dolly.appserivces.sigrunstub.restcom.SigrunStubApiService;
import no.nav.dolly.domain.resultset.RsSigrunnOpprettSkattegrunnlag;

@RunWith(MockitoJUnitRunner.class)
public class SigrunnControllerTest {

    @Mock
    SigrunStubApiService sigrunStubApiService;

    @InjectMocks
    SigrunnController controller;

    @Test
    public void opprettInntekt_happyPath() {
        RsSigrunnOpprettSkattegrunnlag grunn = new RsSigrunnOpprettSkattegrunnlag();

        controller.opprettInntekt(grunn);
        verify(sigrunStubApiService).createSkattegrunnlag(grunn);
    }
}