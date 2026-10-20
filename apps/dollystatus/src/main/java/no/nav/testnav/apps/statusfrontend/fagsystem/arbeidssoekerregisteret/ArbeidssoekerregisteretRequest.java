package no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret;

import java.time.LocalDate;

public record ArbeidssoekerregisteretRequest(
        String identitetsnummer,
        String utfoertAv,
        String kilde,
        String aarsak,
        String nuskode,
        Boolean utdanningBestaatt,
        Boolean utdanningGodkjent,
        String jobbsituasjonsbeskrivelse,
        Jobbsituasjonsdetaljer jobbsituasjonsdetaljer,
        Boolean helsetilstandHindrerArbeid,
        Boolean andreForholdHindrerArbeid
) {

    public record Jobbsituasjonsdetaljer(
            LocalDate gjelderFraDato,
            LocalDate gjelderTilDato,
            Integer stillingStyrk08,
            String stillingstittel,
            Integer stillingsprosent,
            LocalDate sisteDagMedLoenn,
            LocalDate sisteArbeidsdag
    ) {
    }
}
