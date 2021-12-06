package no.nav.dolly.bestilling.pdlforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdresse.OppholdAnnetSted;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresseHistorikk;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpplysning.Master;
import no.nav.dolly.domain.resultset.tpsf.InnvandretUtvandret;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient.PERSON;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresse.UtenlandskAdresse;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.resultset.tpsf.InnvandretUtvandret.InnUtvandret.UTVANDRET;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Component
public class PdlOppholdsadresseMappingStrategy implements MappingStrategy {

    private static LocalDate getGyldigTilDato(LocalDateTime timestamp) {

        return nonNull(timestamp) ? timestamp.toLocalDate() : LocalDate.now().minusDays(1);
    }

    private static void setUtgaatt(PdlOppholdsadresse oppholdsadresse, MappingContext mappingContext) {

        var innvandretUtvandret = ((Person) mappingContext.getProperty(PERSON)).getInnvandretUtvandret().stream()
                .findFirst().orElse(new InnvandretUtvandret());

        oppholdsadresse.setGyldigTilOgMed(UTVANDRET == innvandretUtvandret.getInnutvandret() && "XUK".equals(innvandretUtvandret.getLandkode()) ?
                getGyldigTilDato(innvandretUtvandret.getFlyttedato()) : null);
    }

    private static OppholdAnnetSted mapSpesReg(String spesregCode) {

        if (isBlank(spesregCode)) {
            return null;
        }
        return switch (spesregCode) {
            case "MILI" -> OppholdAnnetSted.MILITAER;
            case "SVAL" -> OppholdAnnetSted.PAA_SVALBARD;
            case "URIK" -> OppholdAnnetSted.UTENRIKS;
            case "PEND" -> OppholdAnnetSted.PENDLER;
            default -> null;
        };
    }

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlOppholdsadresseHistorikk.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlOppholdsadresseHistorikk historikk, MappingContext context) {

                        List<RsPostadresse> postadresser = new ArrayList<>(person.getPostadresse());
                        Collections.reverse(postadresser);

                        historikk.getPdlAdresser().addAll(
                                postadresser.stream()
                                        .filter(RsPostadresse::isUtenlandsk)
                                        .filter(adr -> !person.isKode6())
                                        .map(postadresse -> {
                                            PdlOppholdsadresse oppholdsadresse = new PdlOppholdsadresse();
                                            oppholdsadresse.setUtenlandskAdresse(mapperFacade.map(
                                                    postadresse, UtenlandskAdresse.class));
                                            oppholdsadresse.setKilde(CONSUMER);
                                            oppholdsadresse.setMaster(Master.PDL);
                                            oppholdsadresse.setOppholdAnnetSted(mapSpesReg(person.getSpesreg()));
                                            setUtgaatt(oppholdsadresse, context);
                                            return oppholdsadresse;
                                        })
                                        .collect(Collectors.toList())
                        );
                    }
                })
                .register();

        factory.classMap(RsPostadresse.class, UtenlandskAdresse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsPostadresse postadresse, UtenlandskAdresse utenlandskAdresse, MappingContext context) {

                        StringBuilder adresse = new StringBuilder();
                        if (isNotBlank(postadresse.getPostLinje1())) {
                            adresse.append(postadresse.getPostLinje1())
                                    .append(", ");
                        }
                        if (isNotBlank(postadresse.getPostLinje2())) {
                            adresse.append(postadresse.getPostLinje2())
                                    .append(", ");
                        }
                        if (isNotBlank(postadresse.getPostLinje3())) {
                            adresse.append(postadresse.getPostLinje3())
                                    .append(", ");
                        }
                        utenlandskAdresse.setAdressenavnNummer(adresse.substring(0, adresse.length() - 2));
                        utenlandskAdresse.setLandkode(postadresse.getPostLand());
                    }
                })
                .register();
    }
}
