package no.nav.dolly.bestilling.pdlforvalter.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpplysning.Master;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlVergemaal;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlVergemaal.Omfang;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlVergemaal.Personnavn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlVergemaal.VergeEllerFullmektig;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlVergemaal.VergemaalType;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlVergemaalHistorikk;
import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;

@Slf4j
@Component
@RequiredArgsConstructor
public class PdlVergemaalMappingStrategy implements MappingStrategy {

    private static final String EMBETE_KODEVERK = "Vergemål_Fylkesmannsembeter";
    private final KodeverkConsumer kodeverkConsumer;

    private Omfang getOmfang(String mandatType) {

        if (isNull(mandatType)) {
            return null;
        }

        return switch (mandatType) {
            case "FOR" -> Omfang.UTLENDINGSSAKER_PERSONLIGE_OG_OEKONOMISKE_INTERESSER;
            case "CMB" -> Omfang.PERSONLIGE_OG_OEKONOMISKE_INTERESSER;
            case "FIN" -> Omfang.OEKONOMISKE_INTERESSER;
            case "PER" -> Omfang.PERSONLIGE_INTERESSER;
            default -> null;
        };
    }

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlVergemaalHistorikk.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, PdlVergemaalHistorikk historikk, MappingContext context) {

                        person.getVergemaal().forEach(vergemaal -> {

                            PdlVergemaal pdlVergemaal = new PdlVergemaal();
                            pdlVergemaal.setEmbete(kodeverkConsumer.getKodeverkByName(EMBETE_KODEVERK).get(vergemaal.getEmbete()));
                            pdlVergemaal.setKilde(CONSUMER);
                            pdlVergemaal.setMaster(Master.FREG);
                            pdlVergemaal.setType(getSakstype(vergemaal.getSakType()));
                            pdlVergemaal.setVergeEllerFullmektig(VergeEllerFullmektig.builder()
                                    .motpartsPersonident(vergemaal.getVerge().getIdent())
                                    .navn(mapperFacade.map(vergemaal.getVerge(), Personnavn.class))
                                    .omfang(getOmfang(vergemaal.getMandatType()))
                                    .omfangetErInnenPersonligOmraade(!"FIN".equals(vergemaal.getMandatType()))
                                    .build());

                            historikk.getVergemaaler().add(pdlVergemaal);
                        });
                    }
                })
                .register();
    }

    private static VergemaalType getSakstype(String vergemaalType) {

        if (isNull(vergemaalType)) {
            return null;
        }

        return switch (vergemaalType) {
            case "MIM" -> VergemaalType.MIDLERTIDIG_FOR_MINDREAARIG;
            case "ANN" -> VergemaalType.FORVALTNING_UTENFOR_VERGEMAAL;
            case "VOK" -> VergemaalType.VOKSEN;
            case "MIN" -> VergemaalType.MINDREAARIG;
            case "VOM" -> VergemaalType.MIDLERTIDIG_FOR_VOKSEN;
            case "FRE" -> VergemaalType.STADFESTET_FREMTIDSFULLMAKT;
            case "EMA" -> VergemaalType.ENSLIG_MINDREAARIG_ASYLSOEKER;
            case "EMF" -> VergemaalType.ENSLIG_MINDREAARIG_FLYKTNING;
            default -> null;
        };
    }
}