package no.nav.pdl.forvalter.mapper;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.consumer.GeografiskeKodeverkConsumer;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.dto.PdlVergemaal;
import no.nav.pdl.forvalter.dto.PdlVergemaal.Omfang;
import no.nav.pdl.forvalter.dto.PdlVergemaal.Personnavn;
import no.nav.pdl.forvalter.dto.PdlVergemaal.VergemaalType;
import no.nav.testnav.libs.data.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.VergemaalDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.VergemaalMandattype;
import no.nav.testnav.libs.data.pdlforvalter.v1.VergemaalSakstype;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class VergemaalMappingStrategy implements MappingStrategy {

    private final GeografiskeKodeverkConsumer geografiskeKodeverkConsumer;
    private final PersonRepository personRepository;

    private static Omfang getOmfang(VergemaalMandattype mandatType) {

        if (isNull(mandatType)) {
            return null;
        }

        switch (mandatType) {
            case FOR:
                return Omfang.UTLENDINGSSAKER_PERSONLIGE_OG_OEKONOMISKE_INTERESSER;
            case CMB:
                return Omfang.PERSONLIGE_OG_OEKONOMISKE_INTERESSER;
            case FIN:
                return Omfang.OEKONOMISKE_INTERESSER;
            case PER:
                return Omfang.PERSONLIGE_INTERESSER;
            case ADP:
            default:
                return null;
        }
    }

    private static VergemaalType getSakstype(VergemaalSakstype vergemaalType) {

        if (isNull(vergemaalType)) {
            return null;
        }

        switch (vergemaalType) {
            case MIM:
                return VergemaalType.MIDLERTIDIG_FOR_MINDREAARIG;
            case ANN:
                return VergemaalType.FORVALTNING_UTENFOR_VERGEMAAL;
            case VOK:
                return VergemaalType.VOKSEN;
            case MIN:
                return VergemaalType.MINDREAARIG;
            case VOM:
                return VergemaalType.MIDLERTIDIG_FOR_VOKSEN;
            case FRE:
                return VergemaalType.STADFESTET_FREMTIDSFULLMAKT;
            case EMA:
                return VergemaalType.ENSLIG_MINDREAARIG_ASYLSOEKER;
            case EMF:
                return VergemaalType.ENSLIG_MINDREAARIG_FLYKTNING;
            default:
                return null;
        }
    }

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(VergemaalDTO.class, PdlVergemaal.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(VergemaalDTO kilde, PdlVergemaal destinasjon, MappingContext context) {

                        destinasjon.setEmbete(geografiskeKodeverkConsumer.getEmbeteNavn(kilde.getVergemaalEmbete().name()));
                        destinasjon.setType(getSakstype(kilde.getSakType()));

                        var person = personRepository.findByIdent(kilde.getVergeIdent());
                        NavnDTO personnavn = new NavnDTO();
                        if (person.isPresent()) {
                            personnavn = person.get().getPerson().getNavn().stream().findFirst().orElse(new NavnDTO());
                        }

                        destinasjon.setVergeEllerFullmektig(PdlVergemaal.VergeEllerFullmektig.builder()
                                .motpartsPersonident(kilde.getVergeIdent())
                                .navn(mapperFacade.map(personnavn, Personnavn.class))
                                .omfang(getOmfang(kilde.getMandatType()))
                                .omfangetErInnenPersonligOmraade(VergemaalMandattype.FIN != kilde.getMandatType())
                                .build());
                    }
                })
                .byDefault()
                .register();
    }
}