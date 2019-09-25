package no.nav.dolly.bestilling.instdata;

import static org.assertj.core.util.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.inst.RsInstdata;

@RunWith(MockitoJUnitRunner.class)
public class InstdataClientTest {

    private static final String IDENT = "11111111111";
    private static final NorskIdent NORSK_IDENT = NorskIdent.builder().ident(IDENT).build();
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

        instdataClient.gjenopprett(RsDollyBestilling.builder().build(), NORSK_IDENT, progress);

        assertThat(progress.getInstdataStatus(), is(nullValue()));
    }

    @Test
    public void gjenopprettMiljøIkkeStøttet_SkalGiFeilmedling() {

        BestillingProgress progress = new BestillingProgress();

        when(instdataConsumer.getMiljoer()).thenReturn(ResponseEntity.ok(new String[]{"u5"}));

        instdataClient.gjenopprett(RsDollyBestilling.builder()
                .instdata(newArrayList(RsInstdata.builder().build()))
                .environments(newArrayList("t2"))
                .build(), NORSK_IDENT, progress);

        assertThat(progress.getInstdataStatus(), is(equalTo("t2:Feil: Miljø ikke støttet")));
    }

    @Test
    public void gjenopprettNårInstdataIkkeFinnesFraFoer_SkalGiOk() {

        BestillingProgress progress = new BestillingProgress();

        when(instdataConsumer.getMiljoer()).thenReturn(ResponseEntity.ok(new String[]{"q2"}));
        when(instdataConsumer.deleteInstdata(IDENT, ENVIRONMENT)).thenReturn(ResponseEntity.ok(
                new InstdataResponse[] { InstdataResponse.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .feilmelding("Fant ingen institusjonsopphold på ident.")
                        .build() }));

        when(instdataConsumer.postInstdata(anyList(), eq(ENVIRONMENT))).thenReturn(
                ResponseEntity.ok(new InstdataResponse[] { InstdataResponse.builder()
                        .status(HttpStatus.CREATED).build() })
        );

        instdataClient.gjenopprett(RsDollyBestilling.builder()
                .instdata(newArrayList(RsInstdata.builder().build()))
                .environments(newArrayList("q2"))
                .build(), NORSK_IDENT, progress);

        assertThat(progress.getInstdataStatus(), is(equalTo("q2:opphold=1$OK")));
    }
}