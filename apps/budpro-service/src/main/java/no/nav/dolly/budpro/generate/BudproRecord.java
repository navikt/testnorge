package no.nav.dolly.budpro.generate;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BudproRecord(
        int aga,
        String ansattnavn,
        String ansettelsestype,
        String aarsloenn,
        String beredskap,
        String kode,
        String datoFraStilling,
        String datoFraStillingUtlaan,
        String datoTilStilling,
        String datoTilStillingUtlaan,
        String felles,
        String fellesBeskrivelse,
        String foedselsdato,
        String koststed,
        String koststedBeskrivelse,
        String lederT,
        String ledersNavn,
        String ledersRessursnummer,
        String midlertidigLoennstilskudd,
        String oppgave,
        String oppgaveBeskrivelse,
        String orgenhet,
        String orgenhetT,
        String permisjon,
        String produkt,
        String produktBeskrivelse,
        String refusjoner,
        String ressurs,
        String ressursnummer,
        String sluttetDato,
        String statskonto,
        String stillingsnummer,
        String stillingsprosent,
        String utlaansprosent,
        String utlaantTilKoststed,
        String utlaantOppgave
) {
}
