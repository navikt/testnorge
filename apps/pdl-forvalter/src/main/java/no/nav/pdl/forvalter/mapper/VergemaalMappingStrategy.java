package no.nav.pdl.forvalter.mapper;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.dto.PdlVergemaal;
import no.nav.pdl.forvalter.dto.PdlVergemaal.Omfang;
import no.nav.pdl.forvalter.dto.PdlVergemaal.Personnavn;
import no.nav.pdl.forvalter.dto.PdlVergemaal.VergemaalType;
import no.nav.testnav.libs.data.pdlforvalter.v1.FolkeregistermetadataDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.VergemaalDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.VergemaalMandattype;
import no.nav.testnav.libs.data.pdlforvalter.v1.VergemaalSakstype;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class VergemaalMappingStrategy implements MappingStrategy {

    private final KodeverkConsumer kodeverkConsumer;
    private final PersonRepository personRepository;

    private static Omfang getOmfang(VergemaalMandattype mandatType) {

        if (isNull(mandatType)) {
            return null;
        }

        return switch (mandatType) {
            case FOR -> Omfang.UTLENDINGSSAKER_PERSONLIGE_OG_OEKONOMISKE_INTERESSER;
            case CMB -> Omfang.PERSONLIGE_OG_OEKONOMISKE_INTERESSER;
            case FIN -> Omfang.OEKONOMISKE_INTERESSER;
            case PER -> Omfang.PERSONLIGE_INTERESSER;
            default -> null;
        };
    }

    private static VergemaalType getSakstype(VergemaalSakstype vergemaalType) {

        if (isNull(vergemaalType)) {
            return null;
        }

        return switch (vergemaalType) {
            case MIM -> VergemaalType.MIDLERTIDIG_FOR_MINDREAARIG;
            case ANN -> VergemaalType.FORVALTNING_UTENFOR_VERGEMAAL;
            case VOK -> VergemaalType.VOKSEN;
            case MIN -> VergemaalType.MINDREAARIG;
            case VOM -> VergemaalType.MIDLERTIDIG_FOR_VOKSEN;
            case FRE -> VergemaalType.STADFESTET_FREMTIDSFULLMAKT;
            case EMA -> VergemaalType.ENSLIG_MINDREAARIG_ASYLSOEKER;
            case EMF -> VergemaalType.ENSLIG_MINDREAARIG_FLYKTNING;
        };
    }

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(VergemaalDTO.class, PdlVergemaal.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(VergemaalDTO kilde, PdlVergemaal destinasjon, MappingContext context) {

                        destinasjon.setEmbete(kodeverkConsumer.getEmbeteNavn(kilde.getVergemaalEmbete().name()));
                        destinasjon.setType(getSakstype(kilde.getSakType()));

                        var personnavn = personRepository.findByIdent(kilde.getVergeIdent())
                                .flatMap(person ->
                                        person.getPerson().getNavn().stream().findFirst())
                                .orElse(new NavnDTO());

                        destinasjon.setVergeEllerFullmektig(PdlVergemaal.VergeEllerFullmektig.builder()
                                .motpartsPersonident(kilde.getVergeIdent())
                                .navn(mapperFacade.map(personnavn, Personnavn.class))
                                .omfang(getOmfang(kilde.getMandatType()))
                                .omfangetErInnenPersonligOmraade(VergemaalMandattype.FIN != kilde.getMandatType())
                                .build());

                        if (isNull(destinasjon.getFolkeregistermetadata())) {
                            destinasjon.setFolkeregistermetadata(new FolkeregistermetadataDTO());
                        }
                        destinasjon.getFolkeregistermetadata().setGyldighetstidspunkt(kilde.getGyldigFraOgMed());
                        destinasjon.getFolkeregistermetadata().setOpphoerstidspunkt(kilde.getGyldigTilOgMed());
                    }
                })
                .byDefault()
                .register();
    }
}