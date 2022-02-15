package no.nav.testnav.apps.importfratpsfservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.apps.importfratpsfservice.dto.SkdEndringsmeldingTrans1;
import no.nav.testnav.apps.importfratpsfservice.utils.DatoFraIdentUtility;
import no.nav.testnav.apps.importfratpsfservice.utils.KjoennFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class PersonMappingStrategy implements MappingStrategy {

    private static final String EMPTY_VAL = "00000000";
    private static final String UTEN_FAST_BOSTED = "5";
    private static final String LAND = "9";
    private static final DateTimeFormatter DATO_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static LocalDateTime getDate(String dato) {
        return isNotBlank(dato) && !EMPTY_VAL.equals(dato) ? LocalDate.parse(dato, DATO_FORMAT).atStartOfDay() : null;
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

                        target.setIdent(source.getFodselsdato() + source.getPersonnummer());
                        target.getNavn().add(NavnDTO.builder()
                                .fornavn(source.getFornavn())
                                .mellomnavn(source.getMellomnavn())
                                .etternavn(source.getSlektsnavn())
                                .build());
                        if (isNotBlank(source.getFoedekommLand())) {
                            target.getFoedsel().add(FoedselDTO.builder()
                                    .foedeland(LAND.equals(source.getFoedekommLand().substring(0, 1)) ?
                                            LandkodeDekoder.convert(source.getFoedekommLand().substring(1)) : null)
                                    .fodekommune(!LAND.equals(source.getFoedekommLand().substring(0, 1)) ?
                                            source.getFoedekommLand() : null)
                                    .foedested(source.getFoedested())
                                    .foedselsdato(DatoFraIdentUtility.getDato(target.getIdent()).atStartOfDay())
                                    .foedselsaar(DatoFraIdentUtility.getDato(target.getIdent()).getYear())
                                    .build());
                        }
                        target.getKjoenn().add(KjoennDTO.builder()
                                .kjoenn(KjoennFraIdentUtility.getKjoenn(target.getIdent()))
                                .build());
                        if (isNotBlank(source.getDatoDoed())) {
                            target.getDoedsfall().add(DoedsfallDTO.builder()
                                    .doedsdato(getDate(source.getDatoDoed()))
                                    .build());
                        }
                        target.getStatsborgerskap().add(StatsborgerskapDTO.builder()
                                .landkode(LandkodeDekoder.convert(source.getStatsborgerskap()))
                                .bekreftelsesdato(getDate(source.getStatsborgerskapRegdato()))
                                .build());
                        target.getAdressebeskyttelse().add(AdressebeskyttelseDTO.builder()
                                .gradering(getAdressebeskyttelse(source.getSpesRegType()))
                                .build());

                        if (isNotBlank(source.getAdressetype()) || UTEN_FAST_BOSTED.equals(source.getSpesRegType())) {
                            target.getBostedsadresse().add(mapperFacade.map(source, BostedadresseDTO.class));
                        }
                        if (isNotBlank(source.getAdresse1())) {
                            target.getKontaktadresse().add(mapperFacade.map(source, KontaktadresseDTO.class));
                        }
                        if (isNotBlank(source.getInnvandretFraLand())) {
                            target.getInnflytting().add(InnflyttingDTO.builder()
                                    .fraflyttingsland(LandkodeDekoder.convert(source.getInnvandretFraLand()))
                                    .innflyttingsdato(getDate(source.getFraLandFlyttedato()))
                                    .build());
                        }
                        if (isNotBlank(source.getUtvandretTilLand())) {
                            target.getUtflytting().add(UtflyttingDTO.builder()
                                    .tilflyttingsland(LandkodeDekoder.convert(source.getUtvandretTilLand()))
                                    .utflyttingsdato(getDate(source.getUmyndiggjort()))
                                    .build());
                        }
                    }
                })
                .register();
    }
}