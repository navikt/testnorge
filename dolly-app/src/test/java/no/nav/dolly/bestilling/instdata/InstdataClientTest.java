package no.nav.dolly.bestilling.instdata;

import static java.util.Collections.singletonList;
import static org.assertj.core.util.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class InstdataClientTest {

    private static final String IDENT = "11111111111";
    private static final TpsPerson TPS_IDENT = TpsPerson.builder().hovedperson(IDENT).build();
    private static final String ENVIRONMENT = "q2";

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private InstdataConsumer instdataConsumer;

    @InjectMocks
    private InstdataClient instdataClient;

    @Test
    public void gjenopprettUtenInstdata_TomRetur() {

        BestillingProgress progress = new BestillingProgress();

        instdataClient.gjenopprett(new RsDollyBestillingRequest(), TPS_IDENT, progress);

        assertThat(progress.getInstdataStatus(), is(nullValue()));
    }

    @Test
    public void gjenopprettMiljøIkkeStøttet_SkalGiFeilmedling() {

        BestillingProgress progress = new BestillingProgress();

        when(instdataConsumer.getMiljoer()).thenReturn(singletonList("u5"));

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setInstdata(newArrayList(RsInstdata.builder().build()));
        request.setEnvironments(singletonList("t2"));
        instdataClient.gjenopprett(request, TPS_IDENT, progress);

        assertThat(progress.getInstdataStatus(), is(equalTo("t2:Feil: Miljø ikke støttet")));
    }

    @Test
    public void gjenopprettNårInstdataIkkeFinnesFraFoer_SkalGiOk() {

        BestillingProgress progress = new BestillingProgress();

        when(instdataConsumer.getMiljoer()).thenReturn(singletonList("q2"));
        when(instdataConsumer.deleteInstdata(IDENT, ENVIRONMENT)).thenReturn(ResponseEntity.ok(
                new InstdataResponse[] { InstdataResponse.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .feilmelding("Fant ingen institusjonsopphold på ident.")
                        .build() }));

        when(instdataConsumer.postInstdata(anyList(), eq(ENVIRONMENT))).thenReturn(
                ResponseEntity.ok(new InstdataResponse[] { InstdataResponse.builder()
                        .status(HttpStatus.CREATED).build() })
        );

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setInstdata(newArrayList(RsInstdata.builder().build()));
        request.setEnvironments(singletonList("q2"));
        instdataClient.gjenopprett(request, TPS_IDENT, progress);

        assertThat(progress.getInstdataStatus(), is(equalTo("q2:opphold=1$OK")));
    }
}