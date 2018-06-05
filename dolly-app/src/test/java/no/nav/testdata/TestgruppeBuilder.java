package no.nav.testdata;

import lombok.Builder;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.jpa.Testgruppe;
import no.nav.jpa.Testident;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Builder
public class TestgruppeBuilder {

    private Long id;
    private String navn;
    private Bruker opprettetAv;
    private Bruker sistEndretAv;
    private LocalDateTime datoEndret;
    private Team teamtilhoerighet;
    private Set<Testident> testidenter = new HashSet<>();


    public Testgruppe convertToRealTestgruppe(){
        Testgruppe testgruppe = new Testgruppe();
        testgruppe.setDatoEndret(this.datoEndret);
        testgruppe.setTeamtilhoerighet(this.teamtilhoerighet);
        testgruppe.setId(this.id);
        testgruppe.setOpprettetAv(this.opprettetAv);
        testgruppe.setSistEndretAv(this.sistEndretAv);
        testgruppe.setTestidenter(this.testidenter);
        testgruppe.setNavn(this.navn);

        return testgruppe;
    }
}
