package no.nav.dolly.provider.api.config;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.tpsf.RsDollyProps;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/config", produces = MediaType.APPLICATION_JSON_VALUE)
public class EnvironmentPropsController {

    private final ProvidersProps providersProps;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> test(Exception lol, WebRequest request) {

        return new ResponseEntity("herp", HttpStatus.I_AM_A_TEAPOT);
    }

    @GetMapping
    public RsDollyProps getEnvironmentProps() {
        return RsDollyProps.builder()
                .tpsfUrl(providersProps.getTpsf().getUrl())
                .sigrunStubUrl(providersProps.getSigrunStub().getUrl())
                .krrStubUrl(providersProps.getKrrStub().getUrl())
                .udiStubUrl(providersProps.getUdiStub().getUrl())
                .kodeverkUrl(providersProps.getKodeverk().getUrl())
                .arenaForvalterUrl(providersProps.getArenaForvalter().getUrl())
                .instdataUrl(providersProps.getInstdata().getUrl())
                .build();
    }
}
