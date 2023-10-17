package no.nav.dolly.budpro.generate;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BudproRecord(
        String aga,
        String agaBeskrivelse, // Ny.
        //String ansattnavn,
        String ansettelsestype,
        String arbeidsstedKommune,
        //String aarsloenn,
        //String beredskap,
        //String kode,
        //String datoFraStilling,
        //String datoFraStillingUtlaan,
        //String datoTilStilling,
        //String datoTilStillingUtlaan,
        String felles,
        String fellesBeskrivelse,
        String fraDato, // Ny.
        String foedselsdato,
        String koststed,
        String koststedBeskrivelse,
        String koststedUtlaantFra, // Ny.
        String koststedUtlaantFraBeskrivelse, // Ny.
        String lederUtlaantFra, // Ny.
        //String lederT,
        String ledersNavn,
        String ledersRessursnummer,
        String navn, // Ny.
        //String midlertidigLoennstilskudd,
        String oppgave,
        String oppgaveBeskrivelse,
        String oppgaveUtlaantFra, // Ny.
        String oppgaveUtlaantFraBeskrivelse, // Ny.
        String orgenhet,
        String orgenhetNavn,
        //String orgenhetT,
        //String permisjon,
        String permisjonskode, // Ny.
        String produkt,
        String produktBeskrivelse,
        String produktUtlaantFra, // Ny.
        String produktUtlaantFraBeskrivelse, // Ny.
        //String refusjoner,
        //String ressurs,
        String ressursnummer,
        String skattekommune,
        String sluttetDato,
        String statskonto,
        String statskontoKapittel, // Ny.
        String statskontoPost, // Ny.
        String stillingsnummer,
        String stillingsprosent,
        String tilDato, // Ny.
        //String utlaansprosent,
        //String utlaantTilKoststed,
        //String utlaantOppgave,
        String aarsloennInklFasteTillegg // Ny.
) {
}
