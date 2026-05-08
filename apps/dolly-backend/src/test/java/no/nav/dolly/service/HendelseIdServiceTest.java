package no.nav.dolly.service;

import no.nav.dolly.domain.projection.HendelseIdFragment;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HendelseIdServiceTest {

    private static final String IDENT = "12345678901";

    @Mock
    private BestillingProgressRepository bestillingProgressRepository;

    @Spy
    private JsonMapper jsonMapper = JsonMapper.builder().build();

    @InjectMocks
    private HendelseIdService hendelseIdService;

    @Test
    void shouldReturnJsonNodeWhenIdentExists() {

        var json = """
                {"hovedperson":{"ordrer":[]}}""";
        var fragment = HendelseIdFragment.builder()
                .ident(IDENT)
                .pdlOrdreStatus(json)
                .build();
        when(bestillingProgressRepository.findHendelseIdFragmentByIdent(IDENT))
                .thenReturn(Flux.just(fragment));

        StepVerifier.create(hendelseIdService.getOrdreStatus(IDENT))
                .assertNext(node -> assertThat(node.path("hovedperson").path("ordrer").isArray()).isTrue())
                .verifyComplete();
    }

    @Test
    void shouldThrowNotFoundWhenIdentDoesNotExist() {

        when(bestillingProgressRepository.findHendelseIdFragmentByIdent(IDENT))
                .thenReturn(Flux.empty());

        StepVerifier.create(hendelseIdService.getOrdreStatus(IDENT))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(NotFoundException.class);
                    assertThat(error.getMessage()).contains(IDENT);
                })
                .verify();
    }

    @Test
    void shouldThrowNotFoundWhenPdlOrdreStatusIsBlank() {

        var fragment = HendelseIdFragment.builder()
                .ident(IDENT)
                .pdlOrdreStatus("")
                .build();
        when(bestillingProgressRepository.findHendelseIdFragmentByIdent(IDENT))
                .thenReturn(Flux.just(fragment));

        StepVerifier.create(hendelseIdService.getOrdreStatus(IDENT))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(NotFoundException.class);
                    assertThat(error.getMessage()).contains("HendelseId");
                })
                .verify();
    }

    @Test
    void shouldUseFirstFragmentWhenMultipleExist() {

        var first = HendelseIdFragment.builder()
                .ident(IDENT)
                .pdlOrdreStatus("""
                        {"source":"first"}""")
                .build();
        var second = HendelseIdFragment.builder()
                .ident(IDENT)
                .pdlOrdreStatus("""
                        {"source":"second"}""")
                .build();
        when(bestillingProgressRepository.findHendelseIdFragmentByIdent(IDENT))
                .thenReturn(Flux.just(first, second));

        StepVerifier.create(hendelseIdService.getOrdreStatus(IDENT))
                .assertNext(node -> assertThat(node.path("source").asString()).isEqualTo("first"))
                .verifyComplete();
    }

    @Test
    void shouldReturnMatchingOrdreForPdlArtifact() {

        var json = """
                {"hovedperson":{"ordrer":[
                    {"infoElement":"PDL_NAVN","hendelser":[{"id":1}]},
                    {"infoElement":"PDL_KJOENN","hendelser":[{"id":2}]}
                ]}}""";
        var fragment = HendelseIdFragment.builder()
                .ident(IDENT)
                .pdlOrdreStatus(json)
                .build();
        when(bestillingProgressRepository.findHendelseIdFragmentByIdent(IDENT))
                .thenReturn(Flux.just(fragment));

        StepVerifier.create(hendelseIdService.getOrdrerByArtifact(IDENT, PdlArtifact.PDL_NAVN))
                .assertNext(noder -> assertThat(noder.getFirst().path("infoElement").asString()).isEqualTo("PDL_NAVN"))
                .verifyComplete();
    }

    static Stream<String> shouldReturnEmptyListWhenNoOrdrerMatch() {
        return Stream.of(
                """
                {"hovedperson":{"ordrer":[{"infoElement":"PDL_KJOENN","hendelser":[]}]}}""",
                """
                {"hovedperson":{"ordrer":"not_an_array"}}"""
        );
    }

    @ParameterizedTest
    @MethodSource
    void shouldReturnEmptyListWhenNoOrdrerMatch(String json) {

        var fragment = HendelseIdFragment.builder()
                .ident(IDENT)
                .pdlOrdreStatus(json)
                .build();
        when(bestillingProgressRepository.findHendelseIdFragmentByIdent(IDENT))
                .thenReturn(Flux.just(fragment));

        StepVerifier.create(hendelseIdService.getOrdrerByArtifact(IDENT, PdlArtifact.PDL_NAVN))
                .assertNext(noder -> assertThat(noder).isEmpty())
                .verifyComplete();
    }

    @Test
    void shouldPropagateNotFoundFromInnerCall() {

        when(bestillingProgressRepository.findHendelseIdFragmentByIdent(IDENT))
                .thenReturn(Flux.empty());

        StepVerifier.create(hendelseIdService.getOrdrerByArtifact(IDENT, PdlArtifact.PDL_NAVN))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void shouldReturnMatchingHendelseById() {

        var json = """
                {"hovedperson":{"ordrer":[
                    {"infoElement":"PDL_NAVN","hendelser":[{"id":1,"data":"a"},{"id":2,"data":"b"}]}
                ]}}""";
        var fragment = HendelseIdFragment.builder()
                .ident(IDENT)
                .pdlOrdreStatus(json)
                .build();
        when(bestillingProgressRepository.findHendelseIdFragmentByIdent(IDENT))
                .thenReturn(Flux.just(fragment));

        StepVerifier.create(hendelseIdService.getHendelseById(IDENT, PdlArtifact.PDL_NAVN, 2))
                .assertNext(node -> assertThat(node.path("data").asString()).isEqualTo("b"))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenNoMatchingHendelseId() {

        var json = """
                {"hovedperson":{"ordrer":[
                    {"infoElement":"PDL_NAVN","hendelser":[{"id":1},{"id":2}]}
                ]}}""";
        var fragment = HendelseIdFragment.builder()
                .ident(IDENT)
                .pdlOrdreStatus(json)
                .build();
        when(bestillingProgressRepository.findHendelseIdFragmentByIdent(IDENT))
                .thenReturn(Flux.just(fragment));

        StepVerifier.create(hendelseIdService.getHendelseById(IDENT, PdlArtifact.PDL_NAVN, 99))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenHendelserIsMissing() {

        var json = """
                {"hovedperson":{"ordrer":[
                    {"infoElement":"PDL_NAVN"}
                ]}}""";
        var fragment = HendelseIdFragment.builder()
                .ident(IDENT)
                .pdlOrdreStatus(json)
                .build();
        when(bestillingProgressRepository.findHendelseIdFragmentByIdent(IDENT))
                .thenReturn(Flux.just(fragment));

        StepVerifier.create(hendelseIdService.getHendelseById(IDENT, PdlArtifact.PDL_NAVN, 1))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenPdlArtifactNotFound() {

        var json = """
                {"hovedperson":{"ordrer":[
                    {"infoElement":"PDL_KJOENN","hendelser":[{"id":1}]}
                ]}}""";
        var fragment = HendelseIdFragment.builder()
                .ident(IDENT)
                .pdlOrdreStatus(json)
                .build();
        when(bestillingProgressRepository.findHendelseIdFragmentByIdent(IDENT))
                .thenReturn(Flux.just(fragment));

        StepVerifier.create(hendelseIdService.getHendelseById(IDENT, PdlArtifact.PDL_NAVN, 1))
                .verifyComplete();
    }
}
