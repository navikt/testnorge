package no.nav.dolly.testdata.builder;

import lombok.Builder;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;

import java.time.LocalDate;
import java.util.Set;

@Builder
public class TestgruppeBuilder {

    private Long id;
    private String navn;
    private String hensikt;
    private Bruker opprettetAv;
    private Bruker sistEndretAv;
    private LocalDate datoEndret;
    private Team teamtilhoerighet;
    private Set<Testident> testidenter;
    private Set<Bruker> favorisertAv;


    public Testgruppe convertToRealTestgruppe(){
        Testgruppe testgruppe = new Testgruppe();
        testgruppe.setDatoEndret(this.datoEndret);
        testgruppe.setTeamtilhoerighet(this.teamtilhoerighet);
        testgruppe.setId(this.id);
        testgruppe.setHensikt(this.hensikt);
        testgruppe.setOpprettetAv(this.opprettetAv);
        testgruppe.setSistEndretAv(this.sistEndretAv);
        testgruppe.setTestidenter(this.testidenter);
        testgruppe.setNavn(this.navn);
        testgruppe.setFavorisertAv(this.favorisertAv);

        return testgruppe;
    }
}
