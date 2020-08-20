package no.nav.registre.testnorge.sykemelding.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import no.nav.registre.testnorge.sykemelding.consumer.HendelseConsumer;
import no.nav.registre.testnorge.sykemelding.consumer.SyfoConsumer;
import no.nav.registre.testnorge.sykemelding.domain.Sykemelding;

@Service
@RequiredArgsConstructor
public class SykemeldingService {
    private final SyfoConsumer syfoConsumer;
    private final HendelseConsumer hendelseConsumer;

    public void send(Sykemelding sykemelding) {
        syfoConsumer.send(sykemelding);
        hendelseConsumer.registerSykemeldingOpprettetHendelse(
                sykemelding.getIdent(),
                sykemelding.getFom(),
                sykemelding.getTom()
        );
    }
}