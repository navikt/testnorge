package no.nav.dolly.bestilling.bistandsbehov.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.bistandsbehov.dto.BistandVedtakRequestDTO;
import no.nav.dolly.domain.resultset.bistandsbehov.RsBistandsbehovDTO;
import no.nav.dolly.mapper.MappingStrategy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

import static java.time.LocalDateTime.*;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class BistandsbehovMappingStrategy implements MappingStrategy {

    private static final Random ansatt = new SecureRandom();

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsBistandsbehovDTO.class, BistandVedtakRequestDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsBistandsbehovDTO kilde, BistandVedtakRequestDTO destinasjon, MappingContext context) {

                        destinasjon.setFnr((String) context.getProperty("ident"));
                        destinasjon.setOppfolgingsEnhet((String) context.getProperty("norgEnhet"));

                        if (isBlank(destinasjon.getVeilederIdent())) {
                            destinasjon.setVeilederIdent(getRandomAnsatt());
                        }

                        if (isNull(destinasjon.getVedtakFattet())) {
                            destinasjon.setVedtakFattet(now());
                        }

                        
                    }
                })
                .exclude("oppfolgingsEnhet")
                .byDefault()
                .register();
    }

    public static String getRandomAnsatt() {

        return String.format("Z9%05d", ansatt.nextInt(99999));
    }
}
