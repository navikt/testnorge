package no.nav.registre.testnorge.originalpopulasjon.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.libs.dto.statistikk.v1.StatistikkDTO;
import no.nav.registre.testnorge.libs.dto.statistikk.v1.StatistikkType;
import no.nav.registre.testnorge.originalpopulasjon.consumer.StatistikkConsumer;

@RequestMapping("api/v1/original-populasjon")
@RequiredArgsConstructor
@RestController
public class OriginalPopulasjonController {

    private final StatistikkConsumer statistikkConsumer;
    @GetMapping
    public StatistikkDTO helloWorld() {
        var a = statistikkConsumer.getStatistikk(StatistikkType.ANTALL_ARBEIDSTAKERE_SOM_ER_I_ARBEIDSSTYRKEN);

        return a;
    }
}
