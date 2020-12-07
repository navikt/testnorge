package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain;


import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.repository.model.ArbeidsforholdHistorikkModel;

public class ArbeidsforholdHistorikk {
    private static final int HISTORIKK_LENGTH = 5;
    private static final String START_HISTORIKK = "A";
    private static final char NO_DATA = '-';

    private final String arbeidsforholdId;
    private final String historikk;

    public ArbeidsforholdHistorikk(String arbeidsforholdId, String historikk) {
        this.arbeidsforholdId = arbeidsforholdId;
        this.historikk = historikk;
    }

    public ArbeidsforholdHistorikk(String arbeidsforholdId) {
        this.arbeidsforholdId = arbeidsforholdId;
        this.historikk = START_HISTORIKK;
    }

    public ArbeidsforholdHistorikk(ArbeidsforholdHistorikkModel model) {
        this.arbeidsforholdId = model.getArbeidsforholdId();
        StringBuilder builder = new StringBuilder(model
                .getHistorikk()
                .substring(Math.max(model.getHistorikk().length() - HISTORIKK_LENGTH, 0)));

        while (builder.length() < HISTORIKK_LENGTH) {
            builder.insert(0, NO_DATA);
        }
        this.historikk = builder.toString();
    }

    public String getArbeidsforholdId() {
        return arbeidsforholdId;
    }

    public String getHistorikk() {
        return historikk;
    }

    public ArbeidsforholdHistorikkModel toModel() {
        return ArbeidsforholdHistorikkModel
                .builder()
                .arbeidsforholdId(arbeidsforholdId)
                .historikk(historikk)
                .build();
    }
}