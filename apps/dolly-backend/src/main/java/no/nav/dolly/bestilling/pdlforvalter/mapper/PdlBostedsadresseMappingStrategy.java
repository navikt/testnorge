package no.nav.dolly.bestilling.pdlforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlBostedadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlBostedsadresseHistorikk;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlMatrikkeladresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpplysning;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpplysning.Master;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlVegadresse;
import no.nav.dolly.domain.resultset.tpsf.InnvandretUtvandret;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.adresse.BoAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.BoGateadresse;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient.PERSON;
import static no.nav.dolly.bestilling.pdlforvalter.mapper.PdlAdresseMappingStrategy.getCoadresse;
import static no.nav.dolly.bestilling.pdlforvalter.mapper.PdlAdresseMappingStrategy.getDato;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.resultset.tpsf.InnvandretUtvandret.InnUtvandret.UTVANDRET;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;

@Component
public class PdlBostedsadresseMappingStrategy implements MappingStrategy {

    private static PdlBostedadresse prepBoadresse(BoAdresse adresse, MappingContext mappingContext) {

        PdlBostedadresse bostedadresse = new PdlBostedadresse();
        bostedadresse.setKilde(CONSUMER);
        bostedadresse.setMaster(Master.FREG);
        bostedadresse.setGyldigFraOgMed(getDato(adresse.getFlyttedato()));
        bostedadresse.setGyldigTilOgMed(getDato(adresse.getGyldigTilOgMed()));
        bostedadresse.setAngittFlyttedato(getDato(adresse.getFlyttedato()));
        bostedadresse.setCoAdressenavn(getCoadresse(adresse));
        bostedadresse.setAdresseIdentifikatorFraMatrikkelen(adresse.getMatrikkelId());
        var innvandretUtvandret = ((Person)mappingContext.getProperty(PERSON)).getInnvandretUtvandret();
        bostedadresse.setFolkeregistermetadata(UTVANDRET == innvandretUtvandret.stream().findFirst().orElse(new InnvandretUtvandret()).getInnutvandret() ?
                PdlOpplysning.Folkeregistermetadata.builder()
                .gjeldende(false)
                .build() : null);
        return bostedadresse;
    }

    private static void fixGyldigTilDato(List<BoAdresse> bostedsadresse) {
        for (var i = 0; i < bostedsadresse.size(); i++) {
            if (i + 1 < bostedsadresse.size() &&
                    bostedsadresse.get(i + 1).getFlyttedato().minusDays(1).isAfter(
                            bostedsadresse.get(i).getFlyttedato())) {
                bostedsadresse.get(i).setGyldigTilOgMed(
                        bostedsadresse.get(i + 1).getFlyttedato().minusDays(1));
            }
        }
    }

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlBostedsadresseHistorikk.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlBostedsadresseHistorikk historikk, MappingContext context) {

                        List<BoAdresse> bostedsadresse = new ArrayList<>(person.getBoadresse());
                        Collections.sort(bostedsadresse, Comparator.comparing(BoAdresse::getFlyttedato));
                        fixGyldigTilDato(bostedsadresse);

                        historikk.getPdlAdresser().addAll(
                                Stream.of(
                                                bostedsadresse.stream()
                                                        .filter(boAdresse -> isNotTrue(boAdresse.getDeltAdresse()) &&
                                                                person.isUtenFastBopel() && !person.isKode6())
                                                        .map(boAdresse -> {
                                                            PdlBostedadresse bostedadresse = prepBoadresse(boAdresse, context);
                                                            bostedadresse.setUkjentBosted(PdlBostedadresse.UkjentBosted.builder()
                                                                    .bostedskommune(boAdresse.getKommunenr())
                                                                    .build());
                                                            return bostedadresse;
                                                        })
                                                        .collect(Collectors.toList()),
                                                bostedsadresse.stream()
                                                        .filter(boAdresse -> isNotTrue(boAdresse.getDeltAdresse()) &&
                                                                boAdresse.isGateadresse() && !person.isKode6())
                                                        .map(boAdresse -> {
                                                            PdlBostedadresse bostedadresse = prepBoadresse(boAdresse, context);
                                                            bostedadresse.setVegadresse(mapperFacade.map(
                                                                    boAdresse, PdlVegadresse.class));
                                                            return bostedadresse;
                                                        })
                                                        .collect(Collectors.toList()),
                                                bostedsadresse.stream()
                                                        .filter(boAdresse -> isNotTrue(boAdresse.getDeltAdresse()) &&
                                                                boAdresse.isMatrikkeladresse() && !person.isKode6())
                                                        .map(boAdresse -> {
                                                            PdlBostedadresse bostedadresse = prepBoadresse(boAdresse, context);
                                                            bostedadresse.setMatrikkeladresse(mapperFacade.map(
                                                                    boAdresse, PdlMatrikkeladresse.class));
                                                            return bostedadresse;
                                                        })
                                                        .collect(Collectors.toList())
                                        )
                                        .flatMap(Collection::stream)
                                        .collect(Collectors.toList())
                        );
                    }
                })
                .register();
    }
}
