package no.nav.dolly.provider.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.RsDollyProps;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/config", produces = MediaType.APPLICATION_JSON_VALUE)
public class EnvironmentPropsController {

    private final ProvidersProps providersProps;

    @GetMapping
    @ApiOperation(value = "Hent URL til applikasjonene er integrert mot", authorizations = { @Authorization(value = "Bearer token fra bruker") })
    public RsDollyProps getEnvironmentProps() {
        return RsDollyProps.builder()
                .tpsfUrl(providersProps.getTpsf().getUrl())
                .sigrunStubUrl(providersProps.getSigrunStub().getUrl())
                .krrStubUrl(providersProps.getKrrStub().getUrl())
                .udiStubUrl(providersProps.getUdiStub().getUrl())
                .kodeverkUrl(providersProps.getKodeverk().getUrl())
                .arenaForvalterUrl(providersProps.getArenaForvalter().getUrl())
                .instdataUrl(providersProps.getInstdata().getUrl())
                .aaregdataUrl(providersProps.getAaregdata().getUrl())
                .inntektstub(providersProps.getInntektstub().getUrl())
                .build();
    }
}
