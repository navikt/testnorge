package no.nav.registre.testnorge.sykemelding.service;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.sykemelding.consumer.SyfoConsumer;
import no.nav.registre.testnorge.sykemelding.domain.Sykemelding;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldningResponseDTO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SykemeldingService {
    private final SyfoConsumer syfoConsumer;

    public SykemeldningResponseDTO send(Sykemelding sykemelding) {

        return syfoConsumer.send(sykemelding);
    }
}