package no.nav.registre.testnorge.sykemelding.service;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.sykemelding.consumer.SyfoConsumer;
import no.nav.registre.testnorge.sykemelding.consumer.SyfosmreglerConsumer;
import no.nav.registre.testnorge.sykemelding.domain.Sykemelding;
import no.nav.registre.testnorge.sykemelding.dto.ReceivedSykemeldingDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingResponseDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.ValidationResultDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SykemeldingService {

    private final SyfoConsumer syfoConsumer;
    private final SyfosmreglerConsumer syfosmreglerConsumer;

    public SykemeldingResponseDTO send(Sykemelding sykemelding) {

        return syfoConsumer.send(sykemelding);
    }

    public Mono<ValidationResultDTO> validate(Sykemelding sykemelding) {

        // TBD Mapping

        return syfosmreglerConsumer.validate(new ReceivedSykemeldingDTO());
    }
}