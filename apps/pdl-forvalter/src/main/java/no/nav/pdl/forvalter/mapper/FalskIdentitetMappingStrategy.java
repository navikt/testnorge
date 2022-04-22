package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.dto.PdlFalskIdentitet;
import no.nav.pdl.forvalter.dto.PdlFalskIdentitet.IdentifiserendeInformasjon;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FalskIdentitetDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregistermetadataDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@Component
public class FalskIdentitetMappingStrategy implements MappingStrategy {

    private static LocalDate toDate(LocalDateTime timestamp) {

        return nonNull(timestamp) ? timestamp.toLocalDate() : null;
    }

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(FalskIdentitetDTO.class, PdlFalskIdentitet.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(FalskIdentitetDTO kilde, PdlFalskIdentitet destinasjon, MappingContext context) {

                        destinasjon.setFolkeregistermetadata(FolkeregistermetadataDTO.builder()
                                .ajourholdstidspunkt(LocalDate.now())
                                .gyldighetstidspunkt(toDate(kilde.getGyldigFraOgMed()))
                                .opphoerstidspunkt(toDate(kilde.getGyldigTilOgMed()))
                                .build());

                        destinasjon.setRettIdentitet(PdlFalskIdentitet.RettIdentitet.builder()
                                .rettIdentitetErUkjent(kilde.getRettIdentitetErUkjent())
                                .rettIdentitetVedIdentifikasjonsnummer(kilde.getRettIdentitetVedIdentifikasjonsnummer())
                                .rettIdentitetVedOpplysninger(nonNull(kilde.getRettIdentitetVedOpplysninger()) ?
                                        mapperFacade.map(kilde.getRettIdentitetVedOpplysninger(), IdentifiserendeInformasjon.class) :
                                        null)
                                .build());
                    }
                })
                .byDefault()
                .register();
    }
}