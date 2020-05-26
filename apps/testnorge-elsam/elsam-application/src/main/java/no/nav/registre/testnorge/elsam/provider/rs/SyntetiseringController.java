package no.nav.registre.testnorge.elsam.provider.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.elsam.domain.Sykemelding;
import no.nav.registre.testnorge.elsam.exception.InvalidEnvironmentException;
import no.nav.registre.testnorge.elsam.provider.rs.requests.SyntetiserElsamRequest;
import no.nav.registre.testnorge.elsam.service.SyfoMqService;
import no.nav.registre.testnorge.elsam.service.SyntetiseringService;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
@Slf4j
public class SyntetiseringController {

    private final SyntetiseringService syntetiseringService;
    private final SyfoMqService syfoMqService;

    @PostMapping(value = "/generer")
    public List<String> genererSykemeldinger(@RequestBody SyntetiserElsamRequest syntElsamRequest) throws InvalidEnvironmentException {

        var syntetiserteSykmeldinger = syntetiseringService.syntetiserSykemeldinger(
                syntElsamRequest.getAvspillergruppeId(),
                syntElsamRequest.getMiljoe(),
                syntElsamRequest.getAntallIdenter());


        // TODO: finn køNavn basert på miljø

        for (Sykemelding syntetisertSykemelding : syntetiserteSykmeldinger) {
            syfoMqService.opprettSykmeldingNyttMottak(syntElsamRequest.getMiljoe(), syntetisertSykemelding.toXml());
        }

        log.info("{} sykemeldinger ble opprettet.", syntetiserteSykmeldinger.size());

        return syntetiserteSykmeldinger.stream().map(Sykemelding::toXml).collect(Collectors.toList());
    }
}
