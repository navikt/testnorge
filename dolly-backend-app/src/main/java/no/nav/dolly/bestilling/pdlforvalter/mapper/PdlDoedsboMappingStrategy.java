package no.nav.dolly.bestilling.pdlforvalter.mapper;

import static java.time.LocalDate.now;
import static no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlKontaktinformasjonForDoedsbo.Adressat;
import static no.nav.dolly.util.NullcheckUtil.blankcheckSetDefaultValue;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient;
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

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsPdlKontaktinformasjonForDoedsbo.class, PdlKontaktinformasjonForDoedsbo.class)
                .customize(new CustomMapper<RsPdlKontaktinformasjonForDoedsbo, PdlKontaktinformasjonForDoedsbo>() {
                    @Override
                    public void mapAtoB(RsPdlKontaktinformasjonForDoedsbo rsPdlKontaktinformasjonForDoedsbo,
                                        PdlKontaktinformasjonForDoedsbo kontaktinformasjonForDoedsbo, MappingContext context) {

                        if (rsPdlKontaktinformasjonForDoedsbo.getAdressat() instanceof PdlOrganisasjon) {
                            kontaktinformasjonForDoedsbo.setAdressat(Adressat.builder()
                                    .organisasjonSomAdressat(
                                            mapperFacade.map(rsPdlKontaktinformasjonForDoedsbo.getAdressat(), PdlOrganisasjon.class))
                                    .build());
                        } else if (rsPdlKontaktinformasjonForDoedsbo.getAdressat() instanceof PdlAdvokat) {
                            kontaktinformasjonForDoedsbo.setAdressat(Adressat.builder()
                                    .advokatSomAdressat(
                                            mapperFacade.map(rsPdlKontaktinformasjonForDoedsbo.getAdressat(), PdlAdvokat.class))
                                    .build());

                        } else if (rsPdlKontaktinformasjonForDoedsbo.getAdressat() instanceof PdlKontaktpersonMedIdNummer) {
                            kontaktinformasjonForDoedsbo.setAdressat(Adressat.builder()
                                    .kontaktpersonMedIdNummerSomAdressat(
                                            mapperFacade.map(rsPdlKontaktinformasjonForDoedsbo.getAdressat(), PdlKontaktpersonMedIdNummer.class))
                                    .build());

                        } else if (rsPdlKontaktinformasjonForDoedsbo.getAdressat() instanceof RsPdlKontaktpersonUtenIdNummer) {
                            kontaktinformasjonForDoedsbo.setAdressat(Adressat.builder()
                                    .kontaktpersonUtenIdNummerSomAdressat(
                                            mapperFacade.map(rsPdlKontaktinformasjonForDoedsbo.getAdressat(), PdlKontaktpersonUtenIdNummer.class))
                                    .build());
                        }

                        kontaktinformasjonForDoedsbo.setUtstedtDato(nullcheckSetDefaultValue(kontaktinformasjonForDoedsbo.getUtstedtDato(), now()));
                        kontaktinformasjonForDoedsbo.setLandkode(blankcheckSetDefaultValue(kontaktinformasjonForDoedsbo.getLandkode(), "NOR"));

                        kontaktinformasjonForDoedsbo.setKilde(PdlForvalterClient.KILDE);
                    }
                })
                .byDefault()
                .register();
    }
}