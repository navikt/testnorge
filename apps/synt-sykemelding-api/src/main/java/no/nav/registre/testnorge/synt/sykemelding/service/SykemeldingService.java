package no.nav.registre.testnorge.synt.sykemelding.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.common.session.NavSession;
import no.nav.registre.testnorge.dto.synt.sykemelding.v1.SyntSykemeldingDTO;
import no.nav.registre.testnorge.dto.synt.sykemelding.v1.SyntSykemeldingListeDTO;
import no.nav.registre.testnorge.synt.sykemelding.consumer.HelsepersonellConsumer;
import no.nav.registre.testnorge.synt.sykemelding.consumer.SykemeldingConsumer;
import no.nav.registre.testnorge.synt.sykemelding.consumer.SyntSykemeldingHistorikkConsumer;
import no.nav.registre.testnorge.synt.sykemelding.consumer.dto.SyntSykemeldingHistorikkDTO;
import no.nav.registre.testnorge.synt.sykemelding.domain.LegeListe;
import no.nav.registre.testnorge.synt.sykemelding.domain.SykemeldingListe;

@Service
@RequiredArgsConstructor
public class SykemeldingService {
    private final SyntSykemeldingHistorikkConsumer historikkConsumer;
    private final HelsepersonellConsumer helsepersonellConsumer;
    private final SykemeldingConsumer sykemeldingConsumer;

    public void opprettSykemelding(SyntSykemeldingListeDTO liste, NavSession navSession) {
        Map<String, SyntSykemeldingHistorikkDTO> map = historikkConsumer.genererSykemeldinger(liste
                        .getListe()
                        .stream()
                        .collect(Collectors.toMap(value -> value.getPasient().getIdent(), SyntSykemeldingDTO::getStartDato)),
                navSession
        );
        LegeListe legeListe = helsepersonellConsumer.hentLeger(navSession);
        Map<SyntSykemeldingDTO, SyntSykemeldingHistorikkDTO> syntMap = liste
                .getListe()
                .stream()
                .collect(Collectors.toMap(value -> value, value -> map.get(value.getPasient().getIdent())));
        sykemeldingConsumer.opprett(new SykemeldingListe(syntMap, legeListe), navSession);
    }
}