package no.nav.dolly.bestilling.sigrunstub;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SigrunStubClientTest {

    private static final String IDENT = "11111111";

    @Mock
    private SigrunStubConsumer sigrunStubConsumer;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private TransactionHelperService transactionHelperService;

    @InjectMocks
    private SigrunStubClient sigrunStubClient;

    @Test
    void gjenopprett_ingendata() {

        BestillingProgress progress = new BestillingProgress();
        sigrunStubClient.gjenopprett(new RsDollyBestillingRequest(), DollyPerson.builder().hovedperson(IDENT).build(),
                new BestillingProgress(), false);

        assertThat(progress.getSigrunstubStatus(), is(nullValue()));
    }

    @Test
    void gjenopprett_sigrunstub_feiler() {

        var progress = new BestillingProgress();
        when(sigrunStubConsumer.createSkattegrunnlag(anyList())).thenReturn(Mono.just(SigrunstubResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .melding("Feil ...")
                .build()));
        when(errorStatusDecoder.getErrorText(eq(HttpStatus.BAD_REQUEST), anyString())).thenReturn("Feil:");

        var request = new RsDollyBestillingRequest();
        request.setSigrunstub(singletonList(new OpprettSkattegrunnlag()));

        StepVerifier.create(sigrunStubClient.gjenopprett(request,
                                DollyPerson.builder().hovedperson(IDENT).build(), progress, false)
                        .map(ClientFuture::get))
                .expectNext(BestillingProgress.builder()
                        .sigrunstubStatus("Feil:")
                        .build())
                .verifyComplete();
    }

    @Test
    void gjenopprett_sigrunstub_ok() {

        var request = new RsDollyBestillingRequest();
        request.setSigrunstub(singletonList(new OpprettSkattegrunnlag()));

        when(mapperFacade.mapAsList(anyList(), eq(OpprettSkattegrunnlag.class), any(MappingContext.class)))
                .thenReturn(request.getSigrunstub());

        when(sigrunStubConsumer.createSkattegrunnlag(anyList())).thenReturn(Mono.just(SigrunstubResponse.builder()
                .status(HttpStatus.OK)
                .build()));

        when(sigrunStubConsumer.deleteSkattegrunnlag(IDENT)).thenReturn(Mono.just(new SigrunstubResponse()));

        StepVerifier.create(sigrunStubClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT).build(),
                                new BestillingProgress(), true)
                        .map(ClientFuture::get))
                .expectNext(BestillingProgress.builder()
                        .sigrunstubStatus("OK")
                        .build())
                .verifyComplete();

        verify(sigrunStubConsumer).createSkattegrunnlag(anyList());
    }
}