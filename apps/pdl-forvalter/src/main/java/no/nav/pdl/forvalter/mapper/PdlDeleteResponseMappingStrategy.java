package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.dto.PdlBestillingResponse;
import no.nav.pdl.forvalter.dto.SlettPersonResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Component
public class PdlDeleteResponseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(SlettPersonResponse.class, PdlBestillingResponse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SlettPersonResponse kilde, PdlBestillingResponse destinasjon, MappingContext context) {

                        destinasjon.setHendelseId(kilde.getDeletedOpplysninger().keySet().stream().findFirst().orElse(null));
                        if (isNotBlank(kilde.getFeilmelding())) {
                            throw new WebClientResponseException(BAD_REQUEST.value(), "Sletting feilet",
                                    null, ("Sletting feilet: " + kilde.getFeilmelding()).getBytes(StandardCharsets.UTF_8),
                                    StandardCharsets.UTF_8);
                        }
                    }
                })
                .register();
    }
}