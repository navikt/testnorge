package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.InntektstubConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.OrgFasteDataServiceConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.fastedata.Organisasjon;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.inntektstub.Inntekt;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.inntektstub.Inntektsinformasjon;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class InntektService {

    private final Random rand = new Random();

    private final InntektstubConsumer inntektstubConsumer;
    private final OrgFasteDataServiceConsumer orgFasteDataServiceConsumer;

    public boolean opprettetInntektPaaIdentFoerDato(String ident, LocalDate dato) {
        var inntekter = getInntektinformasjoner(ident, dato.minusMonths(1));
        if (inntekter.isEmpty()) {
            return false;
        }

        return !inntektstubConsumer.postInntekter(inntekter).isEmpty();
    }

    public void deleteInntekterPaaIdent(String ident){
        inntektstubConsumer.deleteInntekter(Collections.singletonList(ident));
    }

    private List<Inntektsinformasjon> getInntektinformasjoner(String ident, LocalDate dato) {
        var organisasjon = getTilfeldigOrganisasjon();
        if (isNull(organisasjon)) {
            return Collections.emptyList();
        }

        List<Inntektsinformasjon> infoListe = new ArrayList<>(12);
        var beloep = 15000 + rand.nextInt(90000);
        var inntekt = Inntekt.builder()
                .beloep((double) beloep)
                .inntektstype(Inntekt.InntektType.LOENNSINNTEKT)
                .inngaarIGrunnlagForTrekk(true)
                .utloeserArbeidsgiveravgift(true)
                .fordel("kontantytelse")
                .beskrivelse("fastloenn")
                .tilleggsinformasjon(null)
                .build();

        for (int i = 0; i < 12; i++) {
            String aarManed = dato.minusMonths(i).format(DateTimeFormatter.ofPattern("yyyy-MM"));
            infoListe.add(Inntektsinformasjon.builder()
                    .aarMaaned(aarManed)
                    .norskIdent(ident)
                    .rapporteringsdato(null)
                    .virksomhet(organisasjon.getOrgnummer())
                    .opplysningspliktig(organisasjon.getOverenhet())
                    .inntektsliste(Collections.singletonList(inntekt))
                    .fradragsliste(Collections.emptyList())
                    .forskuddstrekksliste(Collections.emptyList())
                    .arbeidsforholdsliste(Collections.emptyList())
                    .build());
        }

        return infoListe;

    }

    private Organisasjon getTilfeldigOrganisasjon() {
        var organisasjoner = orgFasteDataServiceConsumer.getOrganisasjoner();
        if (organisasjoner.isEmpty()) {
            return null;
        } else {
            return organisasjoner.get(rand.nextInt(organisasjoner.size()));
        }
    }

}
