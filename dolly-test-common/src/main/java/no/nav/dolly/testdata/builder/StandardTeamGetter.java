package no.nav.dolly.testdata.builder;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;

import java.time.LocalDate;

public class StandardTeamGetter {

    public static Team getStandardTeamUtenMedlemmerEllerEier(){
        return TeamBuilder.builder()
                .navn("sTeam")
                .datoOpprettet(LocalDate.now())
                .beskrivelse("sBeskrivelse")
                .build().convertToRealTeam();
    }

    public static Team getStandardTeamUtenMedlemmerMedEier(Bruker eier){
        return TeamBuilder.builder()
                .navn("sTeam")
                .eier(eier)
                .datoOpprettet(LocalDate.now())
                .beskrivelse("sBeskrivelse")
                .build().convertToRealTeam();
    }
}
