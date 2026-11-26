package no.nav.organisasjonforvalter.service;

import no.nav.organisasjonforvalter.consumer.MiljoerServiceConsumer;
import no.nav.organisasjonforvalter.consumer.OrganisasjonServiceConsumer;
import no.nav.organisasjonforvalter.dto.responses.OrdreResponse.StatusEnv;
import no.nav.organisasjonforvalter.jpa.entity.Status;
import no.nav.organisasjonforvalter.jpa.repository.StatusRepository;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static no.nav.organisasjonforvalter.dto.responses.StatusDTO.Status.COMPLETED;
import static no.nav.organisasjonforvalter.dto.responses.StatusDTO.Status.FAILED;
import static no.nav.organisasjonforvalter.dto.responses.StatusDTO.Status.NOT_FOUND;
import static no.nav.organisasjonforvalter.dto.responses.StatusDTO.Status.RUNNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdreStatusServiceTest {

    private static final String ORGNR = "123456789";
    private static final Set<String> MILJOER = Set.of("q1", "q2");

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private MiljoerServiceConsumer miljoerServiceConsumer;

    @Mock
    private OrganisasjonServiceConsumer organisasjonServiceConsumer;

    private OrdreStatusService ordreStatusService;

    @BeforeEach
    void setUp() {
        ordreStatusService = new OrdreStatusService(statusRepository, miljoerServiceConsumer, organisasjonServiceConsumer);
        ReflectionTestUtils.setField(ordreStatusService, "tidsavbrudd", Duration.ofMinutes(20));
        when(miljoerServiceConsumer.getOrgMiljoer()).thenReturn(Mono.just(MILJOER));
    }

    @Test
    void shouldReturnCompletedWhenOrganisasjonIsFoundInMiljoe() {
        oppslag(Flux.just(Map.of("q2", funnet()), Map.of("q1", OrganisasjonDTO.builder().build())));
        when(statusRepository.findAllByOrganisasjonsnummerIn(List.of(ORGNR)))
                .thenReturn(List.of(status(LocalDateTime.now())));

        assertThat(statusFor())
                .singleElement()
                .satisfies(status -> {
                    assertThat(status.getStatus()).isEqualTo(COMPLETED);
                    assertThat(status.getEnvironment()).isEqualTo("q2");
                });
    }

    @Test
    void shouldReturnRunningWhenOrganisasjonIsNotFoundWithinTidsavbrudd() {
        oppslag(Flux.just(Map.of()));
        when(statusRepository.findAllByOrganisasjonsnummerIn(List.of(ORGNR)))
                .thenReturn(List.of(status(LocalDateTime.now().minusMinutes(5))));

        assertThat(statusFor())
                .singleElement()
                .satisfies(status -> assertThat(status.getStatus()).isEqualTo(RUNNING));
    }

    @Test
    void shouldReturnFailedWhenOrganisasjonIsNotFoundAfterTidsavbrudd() {
        oppslag(Flux.just(Map.of()));
        when(statusRepository.findAllByOrganisasjonsnummerIn(List.of(ORGNR)))
                .thenReturn(List.of(status(LocalDateTime.now().minusMinutes(21))));

        assertThat(statusFor())
                .singleElement()
                .satisfies(status -> {
                    assertThat(status.getStatus()).isEqualTo(FAILED);
                    assertThat(status.getDetails()).contains("20 minutter");
                });
    }

    @Test
    void shouldReturnFailedWhenStatusHasNoCreatedAt() {
        oppslag(Flux.just(Map.of()));
        when(statusRepository.findAllByOrganisasjonsnummerIn(List.of(ORGNR)))
                .thenReturn(List.of(status(null)));

        assertThat(statusFor())
                .singleElement()
                .satisfies(status -> assertThat(status.getStatus()).isEqualTo(FAILED));
    }

    @Test
    void shouldReturnRunningWhenOppslagFailsInMiljoeAfterTidsavbrudd() {
        oppslag(Flux.just(Map.of("q2", OrganisasjonDTO.builder().error("503 Service Unavailable").build())));
        when(statusRepository.findAllByOrganisasjonsnummerIn(List.of(ORGNR)))
                .thenReturn(List.of(status(LocalDateTime.now().minusMinutes(21))));

        assertThat(statusFor())
                .singleElement()
                .satisfies(status -> assertThat(status.getStatus()).isEqualTo(RUNNING));
    }

    @Test
    void shouldPropagateErrorWhenOppslagSignalsError() {
        oppslag(Flux.error(new IllegalStateException("Tokenutveksling feilet")));

        assertThatThrownBy(this::statusFor)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldLookUpEachOrganisasjonOnlyOnceAfterTidsavbrudd() {
        oppslag(Flux.just(Map.of()));
        when(statusRepository.findAllByOrganisasjonsnummerIn(List.of(ORGNR)))
                .thenReturn(List.of(status(LocalDateTime.now().minusMinutes(21))));

        statusFor();

        verify(organisasjonServiceConsumer, times(1)).getStatus(anySet(), anySet());
        verify(organisasjonServiceConsumer, times(0)).getStatus(any(String.class), any(String.class));
    }

    @Test
    void shouldReturnNotFoundWhenOrganisasjonHasNoStatusAndIsNotInMiljoe() {
        oppslag(Flux.just(Map.of()));
        when(statusRepository.findAllByOrganisasjonsnummerIn(List.of(ORGNR))).thenReturn(List.of());

        assertThat(statusFor())
                .singleElement()
                .satisfies(status -> assertThat(status.getStatus()).isEqualTo(NOT_FOUND));
    }

    private void oppslag(Flux<Map<String, OrganisasjonDTO>> svar) {
        when(organisasjonServiceConsumer.getStatus(Set.of(ORGNR), MILJOER)).thenReturn(svar);
    }

    private List<StatusEnv> statusFor() {
        return ordreStatusService.getStatus(List.of(ORGNR)).getOrgStatus().get(ORGNR);
    }

    private static OrganisasjonDTO funnet() {
        return OrganisasjonDTO.builder().orgnummer(ORGNR).build();
    }

    private static Status status(LocalDateTime createdAt) {
        return Status.builder()
                .organisasjonsnummer(ORGNR)
                .miljoe("q2")
                .createdAt(createdAt)
                .build();
    }
}
