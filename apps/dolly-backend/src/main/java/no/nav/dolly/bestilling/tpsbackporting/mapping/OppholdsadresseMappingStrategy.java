package no.nav.dolly.bestilling.tpsbackporting.mapping;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdsadresseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@RequiredArgsConstructor
public class OppholdsadresseMappingStrategy implements MappingStrategy {

    private final KodeverkConsumer kodeverkConsumer;

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(OppholdsadresseDTO.class, TpsfBestilling.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(OppholdsadresseDTO source, TpsfBestilling target, MappingContext context) {

                        if (nonNull(source.getVegadresse())) {
                            target.setPostadresse(List.of(RsPostadresse.builder()
                                    .postLinje1(format("%s %s", source.getVegadresse().getAdressenavn(),
                                            source.getVegadresse().getHusnummer()))
                                    .postLinje3(format("%s %s", source.getVegadresse().getPostnummer(),
                                            kodeverkConsumer.getKodeverkByName("Postnummer")
                                                    .get(source.getVegadresse().getPostnummer())))
                                    .build()));

                        } else if (nonNull(source.getMatrikkeladresse())) {
                            target.setPostadresse(List.of(RsPostadresse.builder()
                                    .postLinje1(format("Gaardsnummer: %s, Bruksnummer: %s",
                                            source.getMatrikkeladresse().getGaardsnummer(),
                                            source.getMatrikkeladresse().getBruksenhetsnummer()))
                                    .postLinje2(format("Kommune: %s %s",
                                            source.getMatrikkeladresse().getKommunenummer(),
                                            kodeverkConsumer.getKodeverkByName("Kommuner")
                                                    .get(source.getMatrikkeladresse().getKommunenummer())))
                                    .postLinje3(format("%s %s",
                                            source.getMatrikkeladresse().getPostnummer(),
                                            kodeverkConsumer.getKodeverkByName("Postnummer")
                                                    .get(source.getMatrikkeladresse().getPostnummer())))
                                    .postLand("NOR")
                                    .build()));

                        } else if (nonNull(source.getUtenlandskAdresse())) {
                            target.setPostadresse(List.of(RsPostadresse.builder()
                                    .postLinje1(isNotBlank(source.getUtenlandskAdresse().getAdressenavnNummer()) ?
                                            source.getUtenlandskAdresse().getAdressenavnNummer() :
                                            source.getUtenlandskAdresse().getPostboksNummerNavn())
                                    .postLinje2(String.format("%s %s", source.getUtenlandskAdresse().getBySted(),
                                            source.getUtenlandskAdresse().getPostkode()))
                                    .postLinje3(isNotBlank(source.getUtenlandskAdresse().getRegion()) ?
                                            source.getUtenlandskAdresse().getRegion() :
                                            source.getUtenlandskAdresse().getRegionDistriktOmraade())
                                    .postLand(source.getUtenlandskAdresse().getLandkode())
                                    .build()));
                        }
                    }
                })
                .register();
    }
}
