package no.nav.pdl.forvalter.mapper;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.domain.Folkeregistermetadata;
import no.nav.pdl.forvalter.domain.PdlVergemaal;
import no.nav.pdl.forvalter.domain.PdlVergemaal.Personnavn;
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

                        destinasjon.setFolkeregistermetadata(Folkeregistermetadata.builder()
                                .gyldighetstidspunkt(kilde.getGyldigFom())
                                .opphoerstidspunkt(kilde.getGyldigTom())
                                .build());

                        var personNavn = personRepository.findByIdent(kilde.getVergeIdent()).get()
                                .getPerson().getNavn().stream().findFirst().get();

                        destinasjon.setVergeEllerFullmektig(PdlVergemaal.VergeEllerFullmektig.builder()
                                .motpartsPersonident(kilde.getVergeIdent())
                                .navn(mapperFacade.map(personNavn, Personnavn.class))
                                .omfang(kilde.getOmfang())
                                .omfangetErInnenPersonligOmraade(isNull(kilde.getOmfang()))
                                .build());
                    }
                })
                .byDefault()
                .register();
    }
}