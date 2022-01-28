package no.nav.testnav.apps.syntsykemeldingapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntsykemeldingapi.adapter.ArbeidsforholdAdapter;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.HelsepersonellConsumer;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.HodejegerenConsumer;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.SykemeldingConsumer;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.SyntSykemeldingHistorikkConsumer;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Sykemelding;
import no.nav.testnav.libs.dto.synt.sykemelding.v1.SyntSykemeldingDTO;
import org.springframework.stereotype.Service;

@Slf4j
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