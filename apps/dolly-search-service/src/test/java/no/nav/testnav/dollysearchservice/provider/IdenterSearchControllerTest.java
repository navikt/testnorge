package no.nav.testnav.dollysearchservice.provider;

import no.nav.testnav.dollysearchservice.service.IdenterSearchService;
import no.nav.testnav.libs.dto.dollysearchservice.v1.IdentdataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static no.nav.testnav.libs.securitycore.config.UserConstant.USER_HEADER_JWT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdenterSearchControllerTest {

    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    @Mock
    private IdenterSearchService identerSearchService;

    private IdenterSearchController identerSearchController;

    @BeforeEach
    void setUp() {

        identerSearchController = new IdenterSearchController(JsonMapper.builder().build(), identerSearchService);
    }

    @Test
    void shouldForwardPaginationParameters() {

        when(identerSearchService.getIdenter(eq("ola"), eq(2), eq(20), eq(123), isNull()))
                .thenReturn(Flux.empty());

        StepVerifier.create(identerSearchController.getIdenter(Map.of(), "ola", 2, 20, 123))
                .verifyComplete();

        verify(identerSearchService).getIdenter("ola", 2, 20, 123, null);
    }

    @Test
    void shouldForwardResolvedOrgnrFromJwt() {

        var jwt = "header." + toBase64Url("{\"org\":\"123456789\"}") + ".signature";

        when(identerSearchService.getIdenter(any(), any(Integer.class), any(Integer.class), any(), eq("123456789")))
                .thenReturn(Flux.just(IdentdataDTO.builder().ident("12345678901").build()));

        StepVerifier.create(identerSearchController.getIdenter(Map.of(USER_HEADER_JWT, jwt), "ola", 0, 10, null))
                .expectNextCount(1)
                .verifyComplete();

        verify(identerSearchService).getIdenter("ola", 0, 10, null, "123456789");
    }

    @Test
    void shouldReturnEmptyResultForBlankFragment() {

        StepVerifier.create(identerSearchController.getIdenter(Map.of(), " ", 0, 10, null))
                .verifyComplete();

        verifyNoInteractions(identerSearchService);
    }

    @Test
    void shouldReturnEmptyResultForMissingFragment() {

        StepVerifier.create(identerSearchController.getIdenter(Map.of(), null, 0, 10, null))
                .verifyComplete();

        verifyNoInteractions(identerSearchService);
    }

    private static String toBase64Url(String value) {

        return ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
