package no.nav.dolly.bestilling.pdlforvalter.mapper;

import static java.util.Objects.isNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlVergemaal;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlVergemaal.Omfang;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlVergemaal.Personnavn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlVergemaal.VergeEllerFullmektig;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlVergemaal.VergemaalType;
import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;

@Component
@RequiredArgsConstructor
public class PdlVergemaalMappingStrategy implements MappingStrategy {

    private static final String EMBETE_KODEVERK = "Vergemål_Fylkesmannsembeter";
    private final KodeverkConsumer kodeverkConsumer;

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlVergemaal.class)
                .customize(new CustomMapper<Person, PdlVergemaal>() {
                    @Override
                    public void mapAtoB(Person person, PdlVergemaal vergemaal, MappingContext context) {

                        vergemaal.setEmbete(kodeverkConsumer.getKodeverkByName(EMBETE_KODEVERK).get(person.getVergemaal().get(0).getEmbete()));
                        vergemaal.setKilde(CONSUMER);
                        vergemaal.setType(getSakstype(person.getVergemaal().get(0).getSakType()));
                        vergemaal.setVergeEllerFullmektig(VergeEllerFullmektig.builder()
                                .motpartsPersonident(person.getVergemaal().get(0).getVerge().getIdent())
                                .navn(mapperFacade.map(person.getVergemaal().get(0).getVerge(), Personnavn.class))
                                .omfang(getOmfang(person.getVergemaal().get(0).getMandatType()))
                                .omfangetErInnenPersonligOmraade(isBlank(person.getVergemaal().get(0).getMandatType()))
                                .build());
                    }
                })
                .register();
    }

    private Omfang getOmfang(String mandatType) {

        if (isNull(mandatType)) {
            return null;
        }

        switch (mandatType) {
        case "FOR":
            return Omfang.UTLENDINGSSAKER_PERSONLIGE_OG_OEKONOMISKE_INTERESSER;
        case "CMB":
            return Omfang.PERSONLIGE_OG_OEKONOMISKE_INTERESSER;
        case "FIN":
            return Omfang.OEKONOMISKE_INTERESSER;
        case "PER":
            return Omfang.PERSONLIGE_INTERESSER;
        case "ADP":
        default:
            return null;
        }
    }

    private static VergemaalType getSakstype(String vergemaalType) {

        if (isNull(vergemaalType)) {
            return null;
        }

        switch (vergemaalType) {
        case "MIM":
            return VergemaalType.MIDLERTIDIG_FOR_MINDREAARIG;
        case "ANN":
            return VergemaalType.FORVALTNING_UTENFOR_VERGEMAAL;
        case "VOK":
            return VergemaalType.VOKSEN;
        case "MIN":
            return VergemaalType.MINDREAARIG;
        case "VOM":
            return VergemaalType.MIDLERTIDIG_FOR_VOKSEN;
        case "FRE":
            return VergemaalType.STADFESTET_FREMTIDSFULLMAKT;
        case "EMA":
            return VergemaalType.ENSLIG_MINDREAARIG_ASYLSOEKER;
        case "EMF":
            return VergemaalType.ENSLIG_MINDREAARIG_FLYKTNING;
        default:
            return null;
        }
    }
}