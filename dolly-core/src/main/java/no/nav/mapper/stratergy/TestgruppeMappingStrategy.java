package no.nav.mapper.stratergy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.jpa.Testgruppe;
import no.nav.mapper.MappingStrategy;
import no.nav.resultSet.RsTeamMedIdOgNavn;
import no.nav.resultSet.RsTestgruppe;
import no.nav.resultSet.RsTestident;

import org.springframework.stereotype.Component;

@Component
public class TestgruppeMappingStrategy implements MappingStrategy{

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Testgruppe.class, RsTestgruppe.class)
                .customize(new CustomMapper<Testgruppe, RsTestgruppe>() {
                    @Override
                    public void mapAtoB(Testgruppe testgruppe, RsTestgruppe rsTestgruppe, MappingContext context) {
                        rsTestgruppe.setDatoEndret(testgruppe.getDatoEndret());
                        rsTestgruppe.setId(testgruppe.getId());
                        rsTestgruppe.setNavn(testgruppe.getNavn());
                        rsTestgruppe.setHensikt(testgruppe.getHensikt());
                        rsTestgruppe.setTestidenter(mapperFacade.mapAsSet(testgruppe.getTestidenter(), RsTestident.class));
                        rsTestgruppe.setOpprettetAvNavIdent(testgruppe.getOpprettetAv().getNavIdent());
                        rsTestgruppe.setSistEndretAvNavIdent(testgruppe.getSistEndretAv().getNavIdent());
                        RsTeamMedIdOgNavn rsTeamMedIdOgNavn = new RsTeamMedIdOgNavn();
                        rsTeamMedIdOgNavn.setNavn(testgruppe.getTeamtilhoerighet().getNavn());
                        rsTeamMedIdOgNavn.setId(testgruppe.getTeamtilhoerighet().getId());
                        rsTestgruppe.setTeam(rsTeamMedIdOgNavn);
                    }
                })
                .register();
    }
}
