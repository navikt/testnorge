package no.nav.registre.testnorge.synt.sykemelding.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import no.nav.testnav.libs.dto.synt.sykemelding.v1.SyntSykemeldingDTO;
import no.nav.registre.testnorge.synt.sykemelding.adapter.ArbeidsforholdAdapter;
import no.nav.registre.testnorge.synt.sykemelding.consumer.HelsepersonellConsumer;
import no.nav.registre.testnorge.synt.sykemelding.consumer.HodejegerenConsumer;
import no.nav.registre.testnorge.synt.sykemelding.consumer.SykemeldingConsumer;
import no.nav.registre.testnorge.synt.sykemelding.consumer.SyntSykemeldingHistorikkConsumer;
import no.nav.registre.testnorge.synt.sykemelding.domain.Sykemelding;

@Service
@RequiredArgsConstructor
public class SykemeldingService {
    private final SyntSykemeldingHistorikkConsumer historikkConsumer;
    private final HelsepersonellConsumer helsepersonellConsumer;
    private final SykemeldingConsumer sykemeldingConsumer;
    private final HodejegerenConsumer hodejegerenConsumer;
    private final ArbeidsforholdAdapter arbeidsforholdAdapter;

    public void opprettSykemelding(SyntSykemeldingDTO dto) {
        var pasient = hodejegerenConsumer.getPersondata(dto.getIdent());
        var arbeidsforhold = arbeidsforholdAdapter.getArbeidsforhold(
                dto.getIdent(),
                dto.getOrgnummer(),
                dto.getArbeidsforholdId()
        );

        var historikk = historikkConsumer.genererSykemeldinger(
                dto.getIdent(),
                dto.getStartDato()
        );
        var helsepersonellListe = helsepersonellConsumer.hentHelsepersonell();

        sykemeldingConsumer.opprettSykemelding(
                new Sykemelding(pasient, historikk, dto, helsepersonellListe.getRandomLege(), arbeidsforhold)
        );
    }
}