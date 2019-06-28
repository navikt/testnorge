package no.nav.dolly.mapper.strategy;

import static no.nav.dolly.util.CurrentNavIdentFetcher.getLoggedInNavIdent;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsTeamMedIdOgNavn;
import no.nav.dolly.domain.resultset.RsTestgruppeUtvidet;
import no.nav.dolly.domain.resultset.RsTestidentBestillingId;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestgruppeUtvidetMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Testgruppe.class, RsTestgruppeUtvidet.class)
                .customize(new CustomMapper<Testgruppe, RsTestgruppeUtvidet>() {
                    @Override
                    public void mapAtoB(Testgruppe testgruppe, RsTestgruppeUtvidet testgruppeUtvidet, MappingContext context) {
                        testgruppeUtvidet.setOpprettetAvNavIdent(testgruppe.getOpprettetAv().getNavIdent());
                        testgruppeUtvidet.setSistEndretAvNavIdent(testgruppe.getSistEndretAv().getNavIdent());
                        testgruppeUtvidet.setTeam(RsTeamMedIdOgNavn.builder()
                                .navn(testgruppe.getTeamtilhoerighet().getNavn())
                                .id(testgruppe.getTeamtilhoerighet().getId())
                                .build());
                        testgruppeUtvidet.setAntallIdenter(testgruppe.getTestidenter().size());
                        testgruppeUtvidet.setTestidenter(mapperFacade.mapAsList(testgruppe.getTestidenter(), RsTestidentBestillingId.class));
                        testgruppeUtvidet.setErMedlemAvTeamSomEierGruppe(isMedlem(testgruppe.getTeamtilhoerighet().getMedlemmer()));
                        testgruppeUtvidet.setFavorittIGruppen(!testgruppe.getFavorisertAv().isEmpty());
                    }

                    private boolean isMedlem(List<Bruker> brukere) {
                        boolean isMedlem = false;
                        for (Bruker bruker : brukere) {
                            if (getLoggedInNavIdent().equalsIgnoreCase(bruker.getNavIdent())) {
                                isMedlem = true;
                                break;
                            }
                        }
                        return isMedlem;
                    }
                })
                .byDefault()
                .register();
    }
}