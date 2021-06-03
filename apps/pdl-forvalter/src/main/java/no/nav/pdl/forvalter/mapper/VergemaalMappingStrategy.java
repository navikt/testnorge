package no.nav.pdl.forvalter.mapper;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.domain.Folkeregistermetadata;
import no.nav.pdl.forvalter.domain.PdlVergemaal;
import no.nav.pdl.forvalter.domain.PdlVergemaal.Omfang;
import no.nav.pdl.forvalter.domain.PdlVergemaal.Personnavn;
import no.nav.pdl.forvalter.domain.PdlVergemaal.VergemaalType;
import no.nav.pdl.forvalter.dto.RsVergemaal;
import no.nav.pdl.forvalter.utils.EmbeteService;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class VergemaalMappingStrategy implements MappingStrategy {

    private final EmbeteService embeteService;
    private final PersonRepository personRepository;

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsVergemaal.class, PdlVergemaal.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsVergemaal kilde, PdlVergemaal destinasjon, MappingContext context) {

                        destinasjon.setEmbete(embeteService.getNavn(kilde.getEmbete()));
                        destinasjon.setType(getSakstype(kilde.getSakType()));

                        destinasjon.setFolkeregistermetadata(Folkeregistermetadata.builder()
                                .gyldighetstidspunkt(kilde.getGyldigFom())
                                .opphoerstidspunkt(kilde.getGyldigTom())
                                .build());

                        var personNavn = personRepository.findByIdent(kilde.getVergeIdent()).get()
                                .getPerson().getNavn().stream().findFirst().get();

                        destinasjon.setVergeEllerFullmektig(PdlVergemaal.VergeEllerFullmektig.builder()
                                .motpartsPersonident(kilde.getVergeIdent())
                                .navn(mapperFacade.map(personNavn, Personnavn.class))
                                .omfang(getOmfang(kilde.getMandatType()))
                                .omfangetErInnenPersonligOmraade(!"FIN".equals(kilde.getMandatType()))
                                .build());
                    }
                })
                .byDefault()
                .register();
    }

    private static Omfang getOmfang(String mandatType) {

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