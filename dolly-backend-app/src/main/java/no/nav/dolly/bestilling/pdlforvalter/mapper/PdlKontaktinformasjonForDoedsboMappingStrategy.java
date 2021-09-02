package no.nav.dolly.bestilling.pdlforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktinformasjonForDoedsbo.PersonSomKontakt;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpplysning.Master;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.RsPdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.RsPdlKontaktpersonMedIdNummer;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import static java.time.LocalDateTime.now;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.RsPdlAdvokat.ADVOKAT;
import static no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.RsPdlKontaktpersonMedIdNummer.PERSON_MEDID;
import static no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.RsPdlOrganisasjon.ORGANISASJON;
import static no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.RsRsPdlKontaktpersonUtenIdNummer.PERSON_UTENID;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

@Component
public class PdlKontaktinformasjonForDoedsboMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsPdlKontaktinformasjonForDoedsbo.class, PdlKontaktinformasjonForDoedsbo.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsPdlKontaktinformasjonForDoedsbo kilde,
                                        PdlKontaktinformasjonForDoedsbo destinasjon, MappingContext context) {

                        destinasjon.setAdresse(mapperFacade.map(kilde,
                                PdlKontaktinformasjonForDoedsbo.KontaktinformasjonForDoedsboAdresse.class));

                        if (ORGANISASJON.equals(kilde.getAdressat().getAdressatType())) {

                            destinasjon.setOrganisasjonSomKontakt(
                                    mapperFacade.map(kilde.getAdressat(),
                                            PdlKontaktinformasjonForDoedsbo.OrganisasjonSomKontakt.class));

                        } else if (ADVOKAT.equals(kilde.getAdressat().getAdressatType())) {

                            destinasjon.setAdvokatSomKontakt(
                                    mapperFacade.map(kilde.getAdressat(),
                                            PdlKontaktinformasjonForDoedsbo.OrganisasjonSomKontakt.class));

                        } else if (PERSON_MEDID.equals(kilde.getAdressat().getAdressatType()) ||
                                PERSON_UTENID.equals(kilde.getAdressat().getAdressatType())) {

                            destinasjon.setPersonSomKontakt(
                                    mapperFacade.map(kilde.getAdressat(),
                                            PersonSomKontakt.class));
                        }

                        destinasjon.setAttestutstedelsesdato(nullcheckSetDefaultValue(kilde.getUtstedtDato(), now())
                                .toLocalDate());

                        destinasjon.setKilde(CONSUMER);
                        destinasjon.setMaster(Master.FREG);
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsPdlKontaktpersonMedIdNummer.class, PersonSomKontakt.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsPdlKontaktpersonMedIdNummer kilde,
                                        PersonSomKontakt destinasjon, MappingContext context) {

                        destinasjon.setIdentifikasjonsnummer(kilde.getIdnummer());
                    }
                })
                .byDefault()
                .register();
    }
}