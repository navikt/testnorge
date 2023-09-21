package no.nav.registre.testnorge.sykemelding.service;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.sykemelding.consumer.SyfoConsumer;
import no.nav.registre.testnorge.sykemelding.consumer.SyntElsamConsumer;
import no.nav.registre.testnorge.sykemelding.domain.Sykemelding;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SykemeldingService {
    private final SyfoConsumer syfoConsumer;
    private final SyntElsamConsumer elsamConsumer;

    public void send(Sykemelding sykemelding) {

        elsamConsumer.genererSykemeldinger(sykemelding.getIdent(), sykemelding.getFom());
        syfoConsumer.send(sykemelding);
    }
}