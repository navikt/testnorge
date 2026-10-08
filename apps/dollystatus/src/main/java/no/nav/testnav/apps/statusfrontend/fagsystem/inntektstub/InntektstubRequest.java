package no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub;

import java.util.List;

public record InntektstubRequest(
        String norskIdent,
        String aarMaaned,
        String opplysningspliktig,
        String virksomhet,
        List<Income> inntektsliste
) {

    public record Income(
            String inntektstype,
            Double beloep,
            String beskrivelse,
            String fordel,
            boolean inngaarIGrunnlagForTrekk,
            boolean utloeserArbeidsgiveravgift
    ) {
    }
}
