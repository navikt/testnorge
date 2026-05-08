package no.nav.dolly.provider;

import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.service.HendelseIdService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HendelseIdControllerTest {

    private static final String IDENT = "12345678901";
    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    @Mock
    private HendelseIdService hendelseIdService;

    @InjectMocks
    private HendelseIdController hendelseIdController;

    @Test
    void shouldReturnHendelserForIdent() {

        var jsonNode = JSON_MAPPER.readTree("""
                {"hovedperson":{"ordrer":[]}}""");
        when(hendelseIdService.getHendelserForIdent(IDENT))
                .thenReturn(Mono.just(jsonNode));

        StepVerifier.create(hendelseIdController.getHendelserForIdent(IDENT))
                .assertNext(node -> {
                    assertThat(node.path("hovedperson").path("ordrer").isArray()).isTrue();
                    verify(hendelseIdService).getHendelserForIdent(IDENT);
                })
                .verifyComplete();
    }

    @Test
    void shouldPropagateNotFoundForIdent() {

        when(hendelseIdService.getHendelserForIdent(IDENT))
                .thenReturn(Mono.error(new NotFoundException("Ident %s ikke funnet".formatted(IDENT))));

        StepVerifier.create(hendelseIdController.getHendelserForIdent(IDENT))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void shouldReturnHendelserForOpplysningstype() {

        var jsonNode = JSON_MAPPER.readTree("""
                {"infoElement":"PDL_NAVN","hendelser":[]}""");
        when(hendelseIdService.getHendelserForIdent(IDENT, PdlArtifact.PDL_NAVN))
                .thenReturn(Mono.just(jsonNode));

        StepVerifier.create(hendelseIdController.getHendelserForOpplysningstype(IDENT, PdlArtifact.PDL_NAVN))
                .assertNext(node -> {
                    assertThat(node.path("infoElement").asString()).isEqualTo("PDL_NAVN");
                    verify(hendelseIdService).getHendelserForIdent(IDENT, PdlArtifact.PDL_NAVN);
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyForUnknownOpplysningstype() {

        when(hendelseIdService.getHendelserForIdent(IDENT, PdlArtifact.PDL_KJOENN))
                .thenReturn(Mono.empty());

        StepVerifier.create(hendelseIdController.getHendelserForOpplysningstype(IDENT, PdlArtifact.PDL_KJOENN))
                .verifyComplete();
    }

    @Test
    void shouldReturnHendelserForOpplysningstypeOgId() {

        var jsonNode = JSON_MAPPER.readTree("""
                {"id":42,"data":"test"}""");
        when(hendelseIdService.getHendelserForIdent(IDENT, PdlArtifact.PDL_NAVN, 42))
                .thenReturn(Mono.just(jsonNode));

        StepVerifier.create(hendelseIdController.getHendelserForOpplysningstypeOgId(IDENT, PdlArtifact.PDL_NAVN, 42))
                .assertNext(node -> {
                    assertThat(node.path("id").asInt()).isEqualTo(42);
                    verify(hendelseIdService).getHendelserForIdent(IDENT, PdlArtifact.PDL_NAVN, 42);
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyForUnknownId() {

        when(hendelseIdService.getHendelserForIdent(IDENT, PdlArtifact.PDL_NAVN, 99))
                .thenReturn(Mono.empty());

        StepVerifier.create(hendelseIdController.getHendelserForOpplysningstypeOgId(IDENT, PdlArtifact.PDL_NAVN, 99))
                .verifyComplete();
    }
}
