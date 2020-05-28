package no.nav.dolly.bestilling.pdlforvalter.mapper;

import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlPersonAdresseWrapper.Adressetype.NORSK;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlPersonAdresseWrapper.Adressetype.UTENLANDSK;
import static no.nav.dolly.bestilling.pdlforvalter.mapper.PdlAdresseMappingStrategy.getDato;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse.PostadresseIFrittFormat;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse.UtenlandskAdresseIFrittFormat;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresse.VegadresseForPost;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlPersonAdresseWrapper;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsGateadresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class PdlKontaktadresseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(PdlPersonAdresseWrapper.class, PdlKontaktadresse.class)
                .customize(new CustomMapper<PdlPersonAdresseWrapper, PdlKontaktadresse>() {
                    @Override
                    public void mapAtoB(PdlPersonAdresseWrapper wrapper, PdlKontaktadresse kontaktadresse, MappingContext context) {

                        kontaktadresse.setKilde(CONSUMER);

                        if (NORSK == wrapper.getAdressetype()) {
                            if (!wrapper.getPerson().getBoadresse().isEmpty() &&
                                    !wrapper.getPerson().isUtenFastBopel() &&
                                    "GATE".equals(wrapper.getPerson().getBoadresse().get(0).getAdressetype())) {

                                kontaktadresse.setGyldigFraOgMed(
                                        getDato(wrapper.getPerson().getBoadresse().get(0).getFlyttedato()));

                                kontaktadresse.setVegadresseForPost(mapperFacade.map(
                                        wrapper.getPerson().getBoadresse().get(0), VegadresseForPost.class));

                            } else if (!wrapper.getPerson().getPostadresse().isEmpty()) {
                                kontaktadresse.setPostadresseIFrittFormat(mapperFacade.map(
                                        wrapper.getPerson().getPostadresse().get(0), PostadresseIFrittFormat.class));
                            }
                        }

                        if (UTENLANDSK == wrapper.getAdressetype()) {
                            kontaktadresse.setUtenlandskAdresseIFrittFormat(mapperFacade.map(
                                    wrapper.getPerson().getPostadresse().get(0), UtenlandskAdresseIFrittFormat.class));
                        }
                    }
                })
                .register();

        factory.classMap(RsGateadresse.class, VegadresseForPost.class)
                .customize(new CustomMapper<RsGateadresse, VegadresseForPost>() {
                            @Override
                            public void mapAtoB(RsGateadresse gateadresse, VegadresseForPost vegadresse, MappingContext context) {

                                vegadresse.setAdressekode(gateadresse.getGatekode());
                                vegadresse.setAdressenavn(gateadresse.getGateadresse());
                                vegadresse.setHusnummer(gateadresse.getHusnummer());
                                vegadresse.setPostnummer(gateadresse.getPostnr());
                            }
                        })
                .byDefault()
                .register();

        factory.classMap(RsPostadresse.class, PostadresseIFrittFormat.class)
                .customize(new CustomMapper<RsPostadresse, PostadresseIFrittFormat>() {
                            @Override
                            public void mapAtoB(RsPostadresse postadresse, PostadresseIFrittFormat postadresseIFrittFormat, MappingContext context) {

                                postadresseIFrittFormat.getAdresselinjer().add(postadresse.getPostLinje1());
                                if (isNotBlank(postadresse.getPostLinje2())) {
                                    postadresseIFrittFormat.getAdresselinjer().add(postadresse.getPostLinje2());
                                }
                                if (isNotBlank(postadresse.getPostLinje3())) {
                                    postadresseIFrittFormat.getAdresselinjer().add(postadresse.getPostLinje3());
                                }
                            }
                        })
                .register();

        factory.classMap(RsPostadresse.class, UtenlandskAdresseIFrittFormat.class)
                .customize(new CustomMapper<RsPostadresse, UtenlandskAdresseIFrittFormat>() {
                            @Override
                            public void mapAtoB(RsPostadresse postadresse, UtenlandskAdresseIFrittFormat utenlandskAdresse, MappingContext context) {

                                utenlandskAdresse.getAdresselinjer().add(postadresse.getPostLinje1());
                                if (isNotBlank(postadresse.getPostLinje2())) {
                                    utenlandskAdresse.getAdresselinjer().add(postadresse.getPostLinje2());
                                }
                                if (isNotBlank(postadresse.getPostLinje3())) {
                                    utenlandskAdresse.getAdresselinjer().add(postadresse.getPostLinje3());
                                }
                                utenlandskAdresse.setLandkode(postadresse.getPostLand());
                            }
                        })
                .register();
    }
}
