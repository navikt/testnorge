package no.nav.dolly.mapper.strategy;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlAdvokat;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlKontaktpersonMedIdNummer;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlKontaktpersonUtenIdNummer;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlOrganisasjon;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.RsPdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.RsPdlKontaktpersonUtenIdNummer;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class PdlDoedsboMappingStrategy implements MappingStrategy {

    @Override public void register(MapperFactory factory) {
        factory.classMap(RsPdlKontaktinformasjonForDoedsbo.class, PdlKontaktinformasjonForDoedsbo.class)
                .customize(new CustomMapper<RsPdlKontaktinformasjonForDoedsbo, PdlKontaktinformasjonForDoedsbo>() {
                    @Override public void mapAtoB(RsPdlKontaktinformasjonForDoedsbo rsPdlKontaktinformasjonForDoedsbo,
                            PdlKontaktinformasjonForDoedsbo pdlKontaktinformasjonForDoedsbo, MappingContext context) {

                        if (rsPdlKontaktinformasjonForDoedsbo.getAdressat() instanceof PdlOrganisasjon) {
                            pdlKontaktinformasjonForDoedsbo.setOrganisasjonSomAdressat(
                                    mapperFacade.map(rsPdlKontaktinformasjonForDoedsbo.getAdressat(), PdlOrganisasjon.class));

                        } else if (rsPdlKontaktinformasjonForDoedsbo.getAdressat() instanceof PdlAdvokat) {
                            pdlKontaktinformasjonForDoedsbo.setAdvokatSomAdressat(
                                    mapperFacade.map(rsPdlKontaktinformasjonForDoedsbo.getAdressat(), PdlAdvokat.class));

                        } else if (rsPdlKontaktinformasjonForDoedsbo.getAdressat() instanceof PdlKontaktpersonMedIdNummer) {
                            pdlKontaktinformasjonForDoedsbo.setKontaktpersonMedIdNummerSomAdressat(
                                    mapperFacade.map(rsPdlKontaktinformasjonForDoedsbo.getAdressat(), PdlKontaktpersonMedIdNummer.class));

                        } else if (rsPdlKontaktinformasjonForDoedsbo.getAdressat() instanceof RsPdlKontaktpersonUtenIdNummer) {
                            pdlKontaktinformasjonForDoedsbo.setKontaktpersonUtenIdNummerSomAdressat(
                                    mapperFacade.map(rsPdlKontaktinformasjonForDoedsbo.getAdressat(), PdlKontaktpersonUtenIdNummer.class));
                        }
                    }

                })
                .byDefault()
                .register();
    }
}