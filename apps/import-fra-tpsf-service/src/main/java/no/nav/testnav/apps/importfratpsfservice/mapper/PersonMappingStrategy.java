package no.nav.testnav.apps.importfratpsfservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.importfratpsfservice.dto.SkdEndringsmeldingTrans1;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class PersonMappingStrategy implements MappingStrategy {

    private static final String UTEN_FAST_BOSTED = "5";

    private static LocalDateTime getDate(String dato) {
        return Objects.nonNull(dato) ? LocalDate.parse(dato).atStartOfDay() : null;
    }

    private static AdresseBeskyttelse getAdressebeskyttelse(String spesreg) {

        if (isNull(spesreg)) {
            return AdresseBeskyttelse.UGRADERT;
        }
        return switch (spesreg) {
            case "6" -> AdresseBeskyttelse.STRENGT_FORTROLIG;
            case "7" -> AdresseBeskyttelse.FORTROLIG;
            default -> AdresseBeskyttelse.UGRADERT;
        };
    }

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(SkdEndringsmeldingTrans1.class, PersonDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SkdEndringsmeldingTrans1 source, PersonDTO target, MappingContext context) {

                        target.getNavn().add(NavnDTO.builder()
                                .fornavn(source.getFornavn())
                                .mellomnavn(source.getMellomnavn())
                                .etternavn(source.getSlektsnavn())
                                .build());
                        target.getFoedsel().add(FoedselDTO.builder()
                                //TBD
                                .build());
                        target.getDoedsfall().add(DoedsfallDTO.builder()
                                .doedsdato(getDate(source.getDatoDoed()))
                                .build());
                        target.getStatsborgerskap().add(StatsborgerskapDTO.builder()
                                .landkode(source.getStatsborgerskap())
                                .bekreftelsesdato(getDate(source.getStatsborgerskapRegdato()))
                                .build());
                        target.getAdressebeskyttelse().add(AdressebeskyttelseDTO.builder()
                                .gradering(getAdressebeskyttelse(source.getSpesRegType()))
                                .build());

                        if (isNotBlank(source.getAdressetype()) || UTEN_FAST_BOSTED.equals(source.getSpesRegType())) {
                            target.getBostedsadresse().add(mapperFacade.map(source, BostedadresseDTO.class));
                        }


                    }
                })
                .register();
    }
}