package no.nav.dolly.mapper.stratergy;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsTeamMedIdOgNavn;
import no.nav.dolly.domain.resultset.RsTestgruppe;
import no.nav.dolly.domain.resultset.RsTestidentBestillingId;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class TestgruppeMappingStrategy implements MappingStrategy{

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Testgruppe.class, RsTestgruppe.class)
                .customize(new CustomMapper<Testgruppe, RsTestgruppe>() {
                    @Override
                    public void mapAtoB(Testgruppe testgruppe, RsTestgruppe rsTestgruppe, MappingContext context) {
                        rsTestgruppe.setTestidenter(mapperFacade.mapAsSet(testgruppe.getTestidenter(), RsTestidentBestillingId.class));
                        rsTestgruppe.setOpprettetAvNavIdent(testgruppe.getOpprettetAv().getNavIdent());
                        rsTestgruppe.setSistEndretAvNavIdent(testgruppe.getSistEndretAv().getNavIdent());
                        RsTeamMedIdOgNavn rsTeamMedIdOgNavn = new RsTeamMedIdOgNavn();
                        rsTeamMedIdOgNavn.setNavn(testgruppe.getTeamtilhoerighet().getNavn());
                        rsTeamMedIdOgNavn.setId(testgruppe.getTeamtilhoerighet().getId());
                        rsTestgruppe.setTeam(rsTeamMedIdOgNavn);
                    }
                })
                .byDefault()
                .register();
    }
}
