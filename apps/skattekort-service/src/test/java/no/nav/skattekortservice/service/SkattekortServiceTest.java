package no.nav.skattekortservice.service;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import no.nav.skattekortservice.consumer.SokosSkattekortConsumer;
import no.nav.skattekortservice.dto.v2.OpprettSkattekortRequest;
import no.nav.skattekortservice.mapper.SkattekortRequestMappingStrategy;
import no.nav.testnav.libs.dto.skattekortservice.v1.ArbeidsgiverSkatt;
import no.nav.testnav.libs.dto.skattekortservice.v1.Forskuddstrekk;
import no.nav.testnav.libs.dto.skattekortservice.v1.IdentifikatorForEnhetEllerPerson;
import no.nav.testnav.libs.dto.skattekortservice.v1.Skattekort;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO;
import no.nav.testnav.libs.dto.skattekortservice.v1.Skattekortmelding;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekkode;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekkprosent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkattekortServiceTest {

    @Mock
    private SokosSkattekortConsumer skattekortConsumer;

    private MapperFacade mapperFacade;
    private SkattekortService skattekortService;

    @BeforeEach
    void setUp() {
        var factory = new DefaultMapperFactory.Builder().build();
        new SkattekortRequestMappingStrategy().register(factory);
        mapperFacade = factory.getMapperFacade();
        skattekortService = new SkattekortService(mapperFacade, skattekortConsumer);
    }

    @Test
    void shouldSendSkattekortWithCorrectRequest() {
        var trekkprosent = Trekkprosent.builder()
                .trekkode(Trekkode.LOENN_FRA_HOVEDARBEIDSGIVER)
                .prosentsats(30)
                .build();
        var forskuddstrekk = Forskuddstrekk.builder()
                .trekkprosent(trekkprosent)
                .build();
        var skattekort = Skattekort.builder()
                .forskuddstrekk(List.of(forskuddstrekk))
                .build();
        var skattekortmelding = Skattekortmelding.builder()
                .arbeidstakeridentifikator("12345678901")
                .inntektsaar(2026)
                .skattekort(skattekort)
                .build();
        var arbeidsgiver = ArbeidsgiverSkatt.builder()
                .arbeidsgiveridentifikator(IdentifikatorForEnhetEllerPerson.builder()
                        .organisasjonsnummer("999999999")
                        .build())
                .arbeidstaker(List.of(skattekortmelding))
                .build();
        var request = SkattekortRequestDTO.builder()
                .arbeidsgiver(List.of(arbeidsgiver))
                .build();

        when(skattekortConsumer.sendSkattekort(any(OpprettSkattekortRequest.class)))
                .thenReturn(Mono.just("OK"));

        var result = skattekortService.sendSkattekort(request);

        StepVerifier.create(result)
                .expectNext("OK")
                .verifyComplete();

        ArgumentCaptor<OpprettSkattekortRequest> captor = ArgumentCaptor.forClass(OpprettSkattekortRequest.class);
        verify(skattekortConsumer).sendSkattekort(captor.capture());

        OpprettSkattekortRequest capturedRequest = captor.getValue();
        assertThat(capturedRequest).isNotNull();
        assertThat(capturedRequest.getFnr()).isEqualTo("12345678901");
        assertThat(capturedRequest.getSkattekort()).isNotNull();
        assertThat(capturedRequest.getSkattekort().getInntektsaar()).isEqualTo(2026);
        assertThat(capturedRequest.getSkattekort().getForskuddstrekkList()).hasSize(1);
    }
}
