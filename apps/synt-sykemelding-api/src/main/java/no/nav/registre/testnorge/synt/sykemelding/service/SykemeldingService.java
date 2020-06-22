package no.nav.registre.testnorge.synt.sykemelding.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import no.nav.registre.testnorge.dto.synt.sykemelding.v1.SyntSykemeldingDTO;
import no.nav.registre.testnorge.synt.sykemelding.consumer.HelsepersonellConsumer;
import no.nav.registre.testnorge.synt.sykemelding.consumer.SykemeldingConsumer;
import no.nav.registre.testnorge.synt.sykemelding.consumer.SyntSykemeldingHistorikkConsumer;
import no.nav.registre.testnorge.synt.sykemelding.consumer.dto.SyntSykemeldingHistorikkDTO;
import no.nav.registre.testnorge.synt.sykemelding.domain.LegeListe;
import no.nav.registre.testnorge.synt.sykemelding.domain.Sykemelding;

@Service
@RequiredArgsConstructor
public class SykemeldingService {
    private final SyntSykemeldingHistorikkConsumer historikkConsumer;
    private final HelsepersonellConsumer helsepersonellConsumer;
    private final SykemeldingConsumer sykemeldingConsumer;

    public void opprettSykemelding(SyntSykemeldingDTO sykemeldingDTO) {
        SyntSykemeldingHistorikkDTO historikk = historikkConsumer.genererSykemeldinger(
                sykemeldingDTO.getPasient().getIdent(),
                sykemeldingDTO.getStartDato()
        );
        LegeListe legeListe = helsepersonellConsumer.hentLeger();

        sykemeldingConsumer.opprettSykemelding(
                new Sykemelding(historikk, sykemeldingDTO, legeListe.getRandomLege())
        );
    }
}