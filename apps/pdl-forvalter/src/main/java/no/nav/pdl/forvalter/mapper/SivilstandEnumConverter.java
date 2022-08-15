package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import no.nav.pdl.forvalter.dto.PdlSivilstand;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.SAMBOER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.UGIFT;

@Component
public class SivilstandEnumConverter extends CustomConverter<SivilstandDTO.Sivilstand, PdlSivilstand.Sivilstand> {

    @Override
    public PdlSivilstand.Sivilstand convert(SivilstandDTO.Sivilstand kilde,
                                            Type<? extends PdlSivilstand.Sivilstand> destinasjon,
                                            MappingContext mappingContext) {

        if (isNull(kilde)) {
            return null;
        }

        return PdlSivilstand.Sivilstand.valueOf(kilde == SAMBOER ? UGIFT.name() : kilde.name());
    }
}