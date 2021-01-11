package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain;


import lombok.Getter;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.repository.model.ArbeidsforholdHistorikkModel;

@Getter
public class ArbeidsforholdHistorikk {
    private final String arbeidsforholdId;
    private final String historikk;
    private final String miljo;

    public ArbeidsforholdHistorikk(String arbeidsforholdId, String historikk, String miljo) {
        this.arbeidsforholdId = arbeidsforholdId;
        this.historikk = historikk;
        this.miljo = miljo;
    }

    public ArbeidsforholdHistorikk(ArbeidsforholdHistorikkModel model) {
        this.arbeidsforholdId = model.getArbeidsforholdId();
        this.historikk = model.getHistorikk();
        this.miljo = model.getMiljo();
    }

    public ArbeidsforholdHistorikkModel toModel() {
        return ArbeidsforholdHistorikkModel
                .builder()
                .arbeidsforholdId(arbeidsforholdId)
                .historikk(historikk)
                .miljo(miljo)
                .build();
    }
}