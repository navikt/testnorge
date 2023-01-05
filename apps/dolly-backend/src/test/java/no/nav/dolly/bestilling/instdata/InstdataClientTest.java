package no.nav.dolly.bestilling.instdata;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.dolly.bestilling.instdata.domain.InstitusjonsoppholdRespons;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InstdataClientTest {

    private static final String IDENT = "11111111111";
    private static final DollyPerson TPS_IDENT = DollyPerson.builder().hovedperson(IDENT).build();
    private static final String ENVIRONMENT = "q2";

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @Mock
    private InstdataConsumer instdataConsumer;

    @InjectMocks
    private InstdataClient instdataClient;

    @Test
    public void gjenopprettUtenInstdata_TomRetur() {

        BestillingProgress progress = new BestillingProgress();

        instdataClient.gjenopprett(new RsDollyBestillingRequest(), TPS_IDENT, progress, false);

        assertThat(progress.getInstdataStatus(), is(nullValue()));
    }

    @Disabled
    @Test
    public void gjenopprettNaarInstdataIkkeFinnesFraFoer_SkalGiOk() {

        var progress = new BestillingProgress();

        when(instdataConsumer.getMiljoer()).thenReturn(Mono.just(List.of("q2")));
        when(mapperFacade.mapAsList(anyList(), eq(Instdata.class), any(MappingContext.class))).thenReturn(List.of(Instdata.builder().build()));
        when(instdataConsumer.getInstdata(IDENT, ENVIRONMENT)).thenReturn(Mono.just(InstitusjonsoppholdRespons.builder()
                .institusjonsopphold(Map.of("q2", List.of(new Instdata())))
                .status(HttpStatus.OK)
                .build()));
        var instdataResponse = List.of(InstdataResponse.builder().status(HttpStatus.CREATED).build());
        when(instdataConsumer.postInstdata(anyList(), anyString())).thenReturn(Flux.just(InstdataResponse.builder()
                .status(HttpStatus.OK)
                .build()));

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setInstdata(List.of(RsInstdata.builder().build()));
        request.setEnvironments(singletonList("q2"));
        instdataClient.gjenopprett(request, TPS_IDENT, progress, false);

        assertThat(progress.getInstdataStatus(), is(equalTo("q2:opphold=1$OK")));
    }
}