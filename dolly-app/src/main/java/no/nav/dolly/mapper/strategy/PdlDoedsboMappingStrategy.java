package no.nav.dolly.mapper.strategy;

import static no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlKontaktinformasjonForDoedsbo.Adressat;

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
                            pdlKontaktinformasjonForDoedsbo.setAdressat(Adressat.builder()
                                    .organisasjonSomAdressat(
                                            mapperFacade.map(rsPdlKontaktinformasjonForDoedsbo.getAdressat(), PdlOrganisasjon.class))
                                    .build());
                        } else if (rsPdlKontaktinformasjonForDoedsbo.getAdressat() instanceof PdlAdvokat) {
                            pdlKontaktinformasjonForDoedsbo.setAdressat(Adressat.builder()
                                    .advokatSomAdressat(
                                            mapperFacade.map(rsPdlKontaktinformasjonForDoedsbo.getAdressat(), PdlAdvokat.class))
                                    .build());

                        } else if (rsPdlKontaktinformasjonForDoedsbo.getAdressat() instanceof PdlKontaktpersonMedIdNummer) {
                            pdlKontaktinformasjonForDoedsbo.setAdressat(Adressat.builder()
                                    .kontaktpersonMedIdNummerSomAdressat(
                                            mapperFacade.map(rsPdlKontaktinformasjonForDoedsbo.getAdressat(), PdlKontaktpersonMedIdNummer.class))
                                    .build());

                        } else if (rsPdlKontaktinformasjonForDoedsbo.getAdressat() instanceof RsPdlKontaktpersonUtenIdNummer) {
                            pdlKontaktinformasjonForDoedsbo.setAdressat(Adressat.builder()
                                    .kontaktpersonUtenIdNummerSomAdressat(
                                            mapperFacade.map(rsPdlKontaktinformasjonForDoedsbo.getAdressat(), PdlKontaktpersonUtenIdNummer.class))
                                    .build());
                        }
                    }
                })
                .byDefault()
                .register();
    }
}