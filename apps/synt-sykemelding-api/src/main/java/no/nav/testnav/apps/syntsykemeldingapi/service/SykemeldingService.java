package no.nav.testnav.apps.syntsykemeldingapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntsykemeldingapi.adapter.ArbeidsforholdAdapter;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.HelsepersonellConsumer;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.PdlProxyConsumer;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.SykemeldingConsumer;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.SyntElsamConsumer;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Person;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Sykemelding;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingResponseDTO;
import no.nav.testnav.libs.dto.synt.sykemelding.v1.SyntSykemeldingDTO;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class SykemeldingService {
    private final SyntElsamConsumer syntElsamConsumer;
    private final HelsepersonellConsumer helsepersonellConsumer;
    private final SykemeldingConsumer sykemeldingConsumer;
    private final ArbeidsforholdAdapter arbeidsforholdAdapter;
    private final PdlProxyConsumer pdlProxyConsumer;

    public SykemeldingResponseDTO opprettSykemelding(SyntSykemeldingDTO syntSykemelding) {

        var pdlData = pdlProxyConsumer.getPdlPerson(syntSykemelding.getIdent());
        var pasient = new Person(pdlData);

        var arbeidsforhold = nonNull(syntSykemelding.getOrgnummer()) ?
                arbeidsforholdAdapter.getArbeidsforhold(
                        syntSykemelding.getIdent(),
                        syntSykemelding.getOrgnummer(),
                        syntSykemelding.getArbeidsforholdId()) :
                null;

        var historikk = syntElsamConsumer.genererSykemeldinger(
                syntSykemelding.getIdent(),
                syntSykemelding.getStartDato()
        );
        var helsepersonellListe = helsepersonellConsumer.hentHelsepersonell();

        return sykemeldingConsumer.opprettSykemelding(
                new Sykemelding(pasient, historikk, syntSykemelding, helsepersonellListe.getRandomLege(), arbeidsforhold).toDTO()
        );
    }
}