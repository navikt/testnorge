package no.nav.dolly.provider.api.config;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.tpsf.RsDollyProps;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/config", produces = MediaType.APPLICATION_JSON_VALUE)
public class EnvironmentPropsController {

    private final ProvidersProps providersProps;

    @GetMapping
    public RsDollyProps getEnvironmentProps() {
        return RsDollyProps.builder()
                .tpsfUrl(providersProps.getTpsf().getUrl())
                .sigrunStubUrl(providersProps.getSigrunStub().getUrl())
                .krrStubUrl(providersProps.getKrrStub().getUrl())
                .kodeverkUrl(providersProps.getKodeverk().getUrl())
                .arenaForvalterUrl(providersProps.getArenaForvalter().getUrl())
                .build();
    }
}
