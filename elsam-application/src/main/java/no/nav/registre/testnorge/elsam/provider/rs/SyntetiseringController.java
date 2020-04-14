package no.nav.registre.testnorge.elsam.provider.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.testnorge.elsam.exception.InvalidEnvironmentException;
import no.nav.registre.testnorge.elsam.provider.rs.requests.SyntetiserElsamRequest;
import no.nav.registre.testnorge.elsam.service.MqService;
import no.nav.registre.testnorge.elsam.service.SyntetiseringService;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
@Slf4j
public class SyntetiseringController {

    private final SyntetiseringService syntetiseringService;
    private final MqService mqService;

    @PostMapping(value = "/generer")
    public List<String> genererSykemeldinger(@RequestBody SyntetiserElsamRequest syntElsamRequest) throws InvalidEnvironmentException {

        var syntetiserteSykmeldinger = syntetiseringService.syntetiserSykemeldinger(
                syntElsamRequest.getAvspillergruppeId(),
                syntElsamRequest.getMiljoe(),
                syntElsamRequest.getAntallIdenter());

        log.info("{} sykemeldinger ble opprettet.", syntetiserteSykmeldinger.size());

        // TODO: finn køNavn basert på miljø

        for (String syntetisertSykemelding : syntetiserteSykmeldinger) {
            mqService.opprettSykmeldingNyttMottak(syntElsamRequest.getMiljoe(), syntetisertSykemelding);
        }

        return syntetiserteSykmeldinger;

        // returner en List<String> av de opprettede identene til orkestratoren
    }
}
